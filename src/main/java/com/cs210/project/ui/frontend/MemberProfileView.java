package com.cs210.project.ui.frontend;

import com.cs210.project.config.Session;
import com.cs210.project.models.Account;
import com.cs210.project.models.Member;
import com.cs210.project.models.Person;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

public class MemberProfileView extends VBox {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    public MemberProfileView() {
        setupUI();
    }

    private void setupUI() {
        Account account = Session.getAccount();
        Member member = Session.getMember();
        Person person = account != null ? account.getPerson() : null;

        getStyleClass().add("profile-root");
        setPadding(new Insets(22));
        setSpacing(18);

        VBox hero = new VBox(10);
        hero.getStyleClass().add("profile-hero");

        Label eyebrow = new Label("MEMBER PROFILE");
        eyebrow.getStyleClass().add("profile-eyebrow");
        Label title = new Label(person != null ? value(person.getName()) : "My Profile");
        title.getStyleClass().add("profile-title");
        Label subtitle = new Label(account != null ? "Account @" + value(account.getUsername()) : "Account details");
        subtitle.getStyleClass().add("profile-subtitle");
        hero.getChildren().addAll(eyebrow, title, subtitle);

        HBox content = new HBox(16);
        content.setAlignment(Pos.TOP_LEFT);

        VBox personalCard = createInfoCard("Personal details");
        GridPane personalGrid = createGrid();
        addRow(personalGrid, 0, "Full name", person != null ? person.getName() : null);
        addRow(personalGrid, 1, "Email", person != null ? person.getEmail() : null);
        addRow(personalGrid, 2, "Phone", person != null ? person.getPhone() : null);
        addRow(personalGrid, 3, "Birth date", person != null && person.getBirthDate() != null ? person.getBirthDate().format(DATE_FORMAT) : null);
        addRow(personalGrid, 4, "Address", formatAddress(person));
        personalCard.getChildren().add(personalGrid);

        VBox accountCard = createInfoCard("Account and license");
        GridPane accountGrid = createGrid();
        addRow(accountGrid, 0, "Username", account != null ? account.getUsername() : null);
        addRow(accountGrid, 1, "Role", account != null && account.getRoleType() != null ? account.getRoleType().getLabel() : null);
        addRow(accountGrid, 2, "Account status", account != null && account.getStatus() != null ? account.getStatus().getLabel() : null);
        addRow(accountGrid, 3, "Driver license", member != null ? member.getDriverLicenseNumber() : null);
        addRow(accountGrid, 4, "License expiry", member != null && member.getDriverLicenseExpiry() != null ? member.getDriverLicenseExpiry().format(DATE_FORMAT) : null);
        accountCard.getChildren().add(accountGrid);

        HBox.setHgrow(personalCard, Priority.ALWAYS);
        HBox.setHgrow(accountCard, Priority.ALWAYS);
        content.getChildren().addAll(personalCard, accountCard);

        getChildren().addAll(hero, content);
    }

    private VBox createInfoCard(String titleText) {
        VBox card = new VBox(14);
        card.getStyleClass().add("profile-card");
        Label title = new Label(titleText);
        title.getStyleClass().add("profile-card-title");
        card.getChildren().add(title);
        return card;
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);
        return grid;
    }

    private void addRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("profile-label");
        Label valueNode = new Label(value(value));
        valueNode.getStyleClass().add("profile-value");
        valueNode.setWrapText(true);
        Region spacer = new Region();
        GridPane.setHgrow(spacer, Priority.ALWAYS);
        grid.add(labelNode, 0, row);
        grid.add(spacer, 1, row);
        grid.add(valueNode, 2, row);
    }

    private String formatAddress(Person person) {
        if (person == null) {
            return null;
        }
        StringBuilder address = new StringBuilder();
        appendAddressPart(address, person.getStreetAddress());
        appendAddressPart(address, person.getCity());
        appendAddressPart(address, person.getState());
        appendAddressPart(address, person.getZipcode());
        appendAddressPart(address, person.getCountry());
        return address.length() == 0 ? "N/A" : address.toString();
    }

    private void appendAddressPart(StringBuilder address, String part) {
        if (part == null || part.isBlank()) {
            return;
        }
        if (address.length() > 0) {
            address.append(", ");
        }
        address.append(part);
    }

    private String value(String text) {
        return text == null || text.isBlank() ? "N/A" : text;
    }
}
