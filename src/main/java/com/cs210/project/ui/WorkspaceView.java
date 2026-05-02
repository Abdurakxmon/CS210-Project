package com.cs210.project.ui;

import com.cs210.project.config.Session;
import com.cs210.project.services.SystemTaskService;

public class WorkspaceView extends javafx.scene.layout.StackPane {
    public WorkspaceView(Runnable onLogout) {
        new SystemTaskService().runSystemTasks();
        getChildren().add(Session.isMember()
                ? new FrontendWorkspaceView(onLogout)
                : new BackendWorkspaceView(onLogout));
    }
}
