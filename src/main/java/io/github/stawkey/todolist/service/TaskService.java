package io.github.stawkey.todolist.service;

import io.github.stawkey.todolist.entity.Task;
import io.github.stawkey.todolist.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Page<Task> getTasks(int page, int limit) {
        PageRequest pageRequest = PageRequest.of(page, limit);
        return taskRepository.findAll(pageRequest);
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public Task update(Integer id, Task task) {
        return taskRepository.findById(id).map(t -> {
            t.setTitle(task.getTitle());
            t.setDescription(task.getDescription());
            return taskRepository.save(t);
        }).orElseGet(() -> taskRepository.save(task));
    }

    public void delete(Integer id) {
        taskRepository.deleteById(id);
    }
}
