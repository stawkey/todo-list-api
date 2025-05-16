package io.github.stawkey.todolist.dto;

import io.github.stawkey.todolist.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Task data transfer object")
public record TaskDTO(@Schema(description = "Task ID", example = "1", nullable = true) Integer id,

                      @Schema(description = "User ID who owns this task", example = "42") Integer userId,

                      @Schema(description = "Task title", example = "Complete project report") String title,

                      @Schema(description = "Task description",
                              example = "Finish the quarterly project report") String description,

                      @Schema(description = "Task creation date and time",
                              example = "2025-05-16T10:30:00") LocalDateTime creationDate) {
    public static TaskDTO convertToDTO(Task task) {
        return new TaskDTO(task.getId(), task.getUserId(), task.getTitle(), task.getDescription(),
                task.getCreationDate());
    }
}