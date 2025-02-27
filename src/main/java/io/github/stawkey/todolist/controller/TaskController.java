package io.github.stawkey.todolist.controller;

import io.github.stawkey.todolist.dto.TaskDTO;
import io.github.stawkey.todolist.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/todos")
    public Page<TaskDTO> getTasks(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int limit) {
        return taskService.getTasks(page, limit);
    }

    @PostMapping("/todos")
    public TaskDTO createTask(@RequestBody TaskDTO taskDTO) {
        return taskService.add(taskDTO);
    }

    @PutMapping("/todos/{id}")
    public TaskDTO updateTask(@PathVariable Integer id, @RequestBody TaskDTO taskDTO) {
        return taskService.update(id, taskDTO);
    }

    @DeleteMapping("/todos/{id}")
    public void deleteTask(@PathVariable Integer id) {
        taskService.delete(id);
    }
}
