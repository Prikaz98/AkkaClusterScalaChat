package ui

import cluster.Demon
import javafx.application.{Application, Platform}
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control.{Button, ListView, TextArea}
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import model.ChatModel

class StartWindow extends Application {

  private def startAnotherWindow(main: Demon): Unit = {
    val window = new CommonWindow(main.name, main.meetingManager, main.model)
    window.start()
    Platform.setImplicitExit(true)
  }

  override def start(primaryStage: Stage): Unit = {
    val grid = new GridPane()
    val nameField = new TextArea()
    nameField.setPromptText("Enter your name please")

    val listPath = FXCollections.observableArrayList("2551", "2552", "2553")
    val listView = new ListView[String](listPath)
    var port = "2551"
    val btn = new Button("ОК")
    val scene = new Scene(grid, 300, 300)

    grid.setAlignment(Pos.CENTER)
    grid.setPrefSize(500, 500)
    grid.setHgap(10)
    grid.setVgap(10)
    grid.setPadding(new Insets(25, 25, 25, 25))
    val selectModel = listView.getSelectionModel
    selectModel
      .selectedItemProperty()
      .addListener((_, _, newValue) => port = newValue)

    btn.setOnAction((_: ActionEvent) => initializeChatWindow(primaryStage, nameField, port))

    grid.add(nameField, 0, 0)
    grid.add(listView, 0, 1)
    grid.add(btn, 0, 2)

    primaryStage.setTitle("Selection Window")
    primaryStage.centerOnScreen()
    primaryStage.getIcons.add(new Image("icon.png"))
    primaryStage.setScene(scene)
    primaryStage.show()
  }

  private def initializeChatWindow(primaryStage: Stage, nameField: TextArea, pathValue: String): Unit = {
    val name = nameField.getText
    val model = new ChatModel()
    primaryStage.close()
    Platform.setImplicitExit(false)
    startAnotherWindow(Demon(name, pathValue, model))
  }

  override def stop(): Unit = System.exit(0)

}