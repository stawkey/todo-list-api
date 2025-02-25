package io.github.stawkey.todolist.repository;

import io.github.stawkey.todolist.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
