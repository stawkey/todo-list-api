package io.github.stawkey.todolist.controller;

import io.github.stawkey.todolist.dto.TaskDTO;
import io.github.stawkey.todolist.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Task API", description = "API for managing tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Retrieves a list of tasks with pagination and filtering",
            description = "Returns a page of tasks. Can be filtered by title and sorted. Requires authentication.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved list of tasks",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized if a valid token is not provided")})
    @GetMapping("/todos")
    public Page<TaskDTO> getTasks(
            @Parameter(description = "Page number, default is 0", example = "0") @RequestParam(defaultValue = "0")
            int page,

            @Parameter(description = "Number of tasks per page, default is 10", example = "10")
            @RequestParam(defaultValue = "10") int limit,

            @Parameter(description = "Title to filter tasks by (optional)", example = "Meeting")
            @RequestParam(required = false) String title,

            @Parameter(description = "Sort direction (ASC or DESC, optional)", example = "DESC")
            @RequestParam(required = false) Sort.Direction sortDirection,

            @Parameter(description = "Field name to sort by (e.g., 'title', 'createdAt')", example = "createdAt")
            @RequestParam(required = false) String sortBy) {

        return taskService.getTasks(page, limit, title, sortDirection, sortBy);
    }

    @Operation(summary = "Creates a new task", description = "Requires authentication.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully created task",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class),
                    examples = @ExampleObject(
                            value = "{\"id\":null,\"title\":\"Buy groceries\",\"description\":\"Milk, eggs, " +
                                    "bread\",\"completed\":false}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized if a valid token is not provided")})
    @PostMapping("/todos")
    public TaskDTO createTask(
            @Parameter(description = "New task data", required = true, schema = @Schema(implementation = TaskDTO.class),
                    examples = @ExampleObject(
                            value = "{\"title\":\"Buy groceries\",\"description\":\"Milk, eggs, bread\"," +
                                    "\"completed\":false}")) @RequestBody TaskDTO taskDTO) {

        return taskService.add(taskDTO);
    }

    @Operation(summary = "Updates an existing task", description = "Requires authentication.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully updated task",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDTO.class),
                    examples = @ExampleObject(
                            value = "{\"id\":1,\"title\":\"Buy more groceries\",\"description\":\"Coffee, " +
                                    "sugar, flour\",\"completed\":true}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized if a valid token is not provided"),
            @ApiResponse(responseCode = "404", description = "Task with the given ID not found")})
    @PutMapping("/todos/{id}")
    public TaskDTO updateTask(
            @Parameter(description = "ID of the task to update", required = true, example = "1") @PathVariable
            Integer id,

            @Parameter(description = "Updated task data", required = true,
                    schema = @Schema(implementation = TaskDTO.class), examples = @ExampleObject(
                    value = "{\"title\":\"Buy more groceries\",\"description\":\"Coffee, sugar, flour\"," +
                            "\"completed\":true}")) @RequestBody TaskDTO taskDTO) {

        return taskService.update(id, taskDTO);
    }

    @Operation(summary = "Deletes a task by its ID", description = "Requires authentication.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully deleted task"),
            @ApiResponse(responseCode = "401", description = "Unauthorized if a valid token is not provided"),
            @ApiResponse(responseCode = "404", description = "Task with the given ID not found")})
    @DeleteMapping("/todos/{id}")
    public void deleteTask(
            @Parameter(description = "ID of the task to delete", required = true, example = "1") @PathVariable
            Integer id) {

        taskService.delete(id);
    }
}