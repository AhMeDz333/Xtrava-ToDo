package com.task.ahmedz.xtrava_todo.data.repository;

import android.support.annotation.NonNull;

import com.task.ahmedz.xtrava_todo.data.TodoListData;
import com.task.ahmedz.xtrava_todo.data.TodoModel;
import com.task.ahmedz.xtrava_todo.data.local.TodoDao;
import com.task.ahmedz.xtrava_todo.data.local.TodoDatabase;
import com.task.ahmedz.xtrava_todo.data.remote.ApiRequests;
import com.task.ahmedz.xtrava_todo.data.source.TodoListDataSource;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by ahmed on 10-Jul-17.
 */

public class TodoListRepository implements TodoListDataSource {

	private static TodoListRepository instance;
	private TodoDao todoDao;

	public static TodoListRepository getInstance() {
		if (instance == null)
			instance = new TodoListRepository();

		return instance;
	}

	private TodoListRepository() {
		todoDao = TodoDatabase.getInstance()
				.todoDao();
	}


	@Override
	public Single<TodoListData> getTodoList() {
		return ApiRequests.getTodoList()
				.doOnSuccess(todoListData -> {
					todoDao.deleteAll(todoListData.getTodoModelsAsArray());
					todoDao.insertAll(todoListData.getTodoModelsAsArray());
				})
				.onErrorResumeNext(throwable -> {
					throwable.printStackTrace();
					return todoDao
							.getAll()
							.first(new ArrayList<>())
							.map(todoModels -> new TodoListData(todoModels, false));
				});
	}

	@Override
	public Single<TodoModel> addTodo(@NonNull TodoModel todoModel) {
		return null;
	}

	@Override
	public Completable deleteTodo(@NonNull String todoId) {
		return null;
	}

	@Override
	public Single<TodoListData> refreshTodoList() {
		return ApiRequests.getTodoList();
	}

	@Override
	public Single<TodoModel> updateTodo(TodoModel todoModel) {
		return ApiRequests.updateTodo(todoModel.getId(), todoModel)
				.doOnSuccess(todoDao::update);
	}
}
