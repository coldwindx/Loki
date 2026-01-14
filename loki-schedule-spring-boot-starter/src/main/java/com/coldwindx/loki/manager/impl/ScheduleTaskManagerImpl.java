package com.coldwindx.loki.manager.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.coldwindx.loki.config.ScheduleConfig;
import com.coldwindx.loki.domain.ScheduleTask;
import com.coldwindx.loki.entity.TaskRequest;
import com.coldwindx.loki.entity.TaskResponse;
import com.coldwindx.loki.entity.TaskStatus;
import com.coldwindx.loki.manager.ScheduleTaskManager;
import com.coldwindx.loki.mapper.ScheduleTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ScheduleTaskManagerImpl implements ScheduleTaskManager {

    @Autowired
    private ScheduleTaskMapper mapper;

    @Override
    public <T> TaskResponse<T> select() {
        return null;
    }

    @Override
    public <T> List<TaskResponse<T>> query(String topic, ScheduleConfig config) {
        if(topic == null || topic.trim().isEmpty())
            throw new IllegalArgumentException("topic is null");

        LambdaQueryWrapper<ScheduleTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleTask::getTopic, topic)
                .eq(ScheduleTask::getStatus, TaskStatus.INIT.getCode())
                .last("limit " + config.getExecuteCount());
        List<ScheduleTask> tasks = mapper.selectList(wrapper);
        return tasks.stream().map(this::<T>gettTaskResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public <T> TaskResponse<T> submit(TaskRequest<T> request) {
        this.check(request);

        ScheduleTask task = new ScheduleTask();
        task.setTopic(request.getTopic());
        task.setFingerprint(request.getFingerprint());
        task.setArgs(request.getArgs());
        task.setStatus(TaskStatus.INIT.getCode());

        int count = mapper.insert(task);
        if(count <= 0) return null;

        return this.gettTaskResponse(task);
    }

    private <T> TaskResponse<T> gettTaskResponse(ScheduleTask task) {
        TaskResponse<T> response = new TaskResponse<>();
        response.setId(task.getId());
        response.setTopic(task.getTopic());
        response.setFingerprint(task.getFingerprint());
        try{
            Class<?> clazz = Class.forName(task.getClazz());
            response.setArgs((T) JSON.parseObject(task.getArgs(), clazz));
        } catch (ClassNotFoundException e) {
            log.error("class not found, topic: {}, fingerprint: {}", task.getTopic(), task.getFingerprint(), e);
        }
        response.setStatus(task.getStatus());
        return response;
    }

    @Override
    @Transactional
    public void remove(String topic, String fingerprint) {
        if(topic == null || topic.trim().isEmpty())
            throw new IllegalArgumentException("topic is null");

        if(fingerprint == null || fingerprint.trim().isEmpty())
            throw new IllegalArgumentException("fingerprint is null");

        LambdaQueryWrapper<ScheduleTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleTask::getTopic, topic)
                .eq(ScheduleTask::getFingerprint, fingerprint);
        mapper.delete(wrapper);
    }

    @Override
    @Transactional
    public <T> void lock(TaskResponse<T> response) {
        if(response == null) return;

        this.check(response);

        ScheduleTask task = new ScheduleTask();
        task.setId(response.getId());
        task.setStatus(TaskStatus.EXECUTING.getCode());
        mapper.updateById(task);
    }

    @Override
    @Transactional
    public <T> void lock(List<TaskResponse<T>> responses) {
        if(responses == null || responses.isEmpty()) return;

        this.check(responses.get(0));

        ScheduleTask task = new ScheduleTask();
        task.setStatus(TaskStatus.EXECUTING.getCode());

        List<Long> ids = responses.stream().map(TaskResponse::getId).collect(Collectors.toList());
        LambdaQueryWrapper<ScheduleTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ScheduleTask::getId, ids).eq(ScheduleTask::getStatus, TaskStatus.INIT.getCode());

        mapper.update(task, wrapper);
    }

    @Override
    @Transactional
    public <T> void done(TaskResponse<T> response) {
        if(response == null) return;

        this.check(response);

        ScheduleTask task = new ScheduleTask();
        task.setId(response.getId());
        task.setTopic(response.getTopic());
        task.setFingerprint(response.getFingerprint());
        task.setStatus(TaskStatus.SUCCESS.getCode());
        mapper.updateById(task);
    }

    @Override
    @Transactional
    public <T> void error(TaskResponse<T> response, Throwable throwable) {
        if(response == null) return;

        this.check(response);

        ScheduleTask task = new ScheduleTask();
        task.setId(response.getId());
        task.setTopic(response.getTopic());
        task.setFingerprint(response.getFingerprint());
        task.setStatus(TaskStatus.FAIL.getCode());
        task.setResult(throwable.getMessage().substring(254));
        mapper.updateById(task);
    }

    private <T> void check(TaskRequest<T> request) {
        if (request.getTopic() == null || request.getTopic().isEmpty())
            throw new IllegalArgumentException("topic is empty");

        if (request.getFingerprint() == null || request.getFingerprint().isEmpty())
            throw new IllegalArgumentException("fingerprint is empty");

        if (request.getArgs() == null || request.getArgs().isEmpty())
            throw new IllegalArgumentException("args is empty");
    }

    private <T> void check(TaskResponse<T> response) {
        if (response.getId() == null)
            throw new IllegalArgumentException("id is null");

        if (response.getTopic() == null || response.getTopic().isEmpty())
            throw new IllegalArgumentException("topic is empty");

        if (response.getFingerprint() == null || response.getFingerprint().isEmpty())
            throw new IllegalArgumentException("fingerprint is empty");

    }
}
