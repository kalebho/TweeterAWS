package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.services.UserObserver;
import edu.byu.cs.tweeter.client.model.services.UserService;
import edu.byu.cs.tweeter.client.model.services.PagedObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> implements UserObserver, PagedObserver<T> {

    public static final int PAGE_SIZE = 10;
    protected T lastItem;
    public boolean hasMorePages;
    public boolean isLoading = false;
    protected View view;
    protected User user;

    public PagedPresenter(View view, User user) {
        this.view = view;
        this.user = user;
    }

    public interface View<T> {
        void showInfoMessage(String message);
        void showErrorMessage(String message);
        void openMainView(User user);
        void startingLoadingItems();
        void endLoadingItems();
        void addItems(List<T> items);
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void getUser(AuthToken authToken, String alias) {
        UserService userService = new UserService();
        userService.getUser(authToken, alias, this);
        view.showInfoMessage("Getting user...");
    }

    public void loadMoreItems() {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.startingLoadingItems();
            getData();
        }
    }

    protected abstract void getData();

    @Override
    public void pagedObserverSuccess(List<T> items, boolean hasMorePages) {
        isLoading = false;
        lastItem = (items.size() > 0 ? items.get(items.size() - 1) : null);
        this.hasMorePages = hasMorePages;
        view.endLoadingItems();
        view.addItems(items);
    }


    @Override
    public void getUserSuccessful(User user) {
        view.openMainView(user);
    }

    @Override
    public void handlerFail(String message) {
        view.showErrorMessage(message);
    }



}
