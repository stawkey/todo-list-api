package io.github.stawkey.todolist.controller;

import io.github.stawkey.todolist.entity.Task;
import io.github.stawkey.todolist.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
public class TodoController {
    private final TaskService taskService;

    public TodoController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/todos")
    public Page<Task> getTasks(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int limit) {
        return taskService.getTasks(page, limit);
    }

    @PostMapping("/todos")
    public Task createTask(@RequestBody Task task) {
        return taskService.save(task);
    }

    @PutMapping("/todos/{id}")
    public Task updateTask(@PathVariable Integer id, @RequestBody Task task) {
        return taskService.update(id, task);
    }

    @DeleteMapping("/todos/{id}")
    public void deleteTask(@PathVariable Integer id) {
        taskService.delete(id);
    }
}
