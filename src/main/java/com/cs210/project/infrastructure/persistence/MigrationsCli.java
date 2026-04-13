package com.cs210.project.infrastructure.persistence;

public final class MigrationsCli {

    private MigrationsCli() {
    }

    public static void main(String[] args) {
        try {
            AppDatabase.runMigrations();
            System.out.println("Authentication migrations completed successfully.");
        } catch (Exception exception) {
            System.err.println("Migration run failed: " + exception.getMessage());
            exception.printStackTrace();
            System.exit(1);
        }
    }
}
