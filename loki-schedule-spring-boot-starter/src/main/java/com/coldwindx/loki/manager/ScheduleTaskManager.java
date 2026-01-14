package com.coldwindx.loki.manager;

import com.coldwindx.loki.config.ScheduleConfig;
import com.coldwindx.loki.entity.TaskRequest;
import com.coldwindx.loki.entity.TaskResponse;

import java.util.List;

public interface ScheduleTaskManager {
    <T> TaskResponse<T> select();
    <T> List<TaskResponse<T>> query(String topic, ScheduleConfig config);
    <T> TaskResponse<T> submit(TaskRequest<T> request);
    void remove(String topic, String fingerprint);
    <T> void lock(TaskResponse<T> response);
    <T> void lock(List<TaskResponse<T>> responses);
    <T> void done(TaskResponse<T> response);
    <T> void error(TaskResponse<T> response, Throwable throwable);

}
