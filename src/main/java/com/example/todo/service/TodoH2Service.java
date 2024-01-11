package com.example.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import com.example.todo.model.*;

import com.example.todo.repository.TodoRepository;

@Service
public class TodoH2Service implements TodoRepository {

    @Autowired
    private JdbcTemplate db;

    @Override
    public ArrayList<Todo> getTodos() {
        List<Todo> todoListed = db.query("select * from todolist", new TodoRowMapper());
        ArrayList<Todo> todos = new ArrayList<>(todoListed);
        return todos;
    }

    @Override
    public Todo getTodoById(int id) {
        try {
            Todo todo = db.queryForObject("select * from todolist where id = ?", new TodoRowMapper(), id);
            return todo;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Todo addTodo(Todo todo) {
        db.update("insert into todolist(todo,priority,status) values (?,?,?)", todo.getTodo(), todo.getPriority(),
                todo.getStatus());
        Todo savedTodo = db.queryForObject("select * from todolist where todo = ? and priority = ? and status = ?",
                new TodoRowMapper(),
                todo.getTodo(), todo.getPriority(), todo.getStatus());
        return savedTodo;
    }

    @Override
    public Todo updateTodo(int id, Todo todo) {
        Todo todoOne = getTodoById(id);
        if (todoOne == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (todo.getTodo() != null) {
            db.update("update todolist set todo = ? where id = ?", todo.getTodo(), id);
        }
        if (todo.getPriority() != null) {
            db.update("update todolist set priority = ? where id = ?", todo.getPriority(), id);
        }
        if (todo.getStatus() != null) {
            db.update("update todolist set status = ? where id = ?", todo.getStatus(), id);
        }
        return getTodoById(id);
    }

    @Override
    public void deleteTodo(int id) {
        Todo todo = getTodoById(id);
        if (todo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            db.update("delete from todolist where id = ?", id);
        }
    }
}

