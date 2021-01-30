import StartWindow._
import javafx.application.{Application, Platform}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control.{Button, ListView, TextArea}
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.stage.Stage

class StartWindow extends Application {


  override def start(primaryStage: Stage): Unit = {
    grid.setAlignment(Pos.CENTER)
    grid.setPrefSize(500,500)
    grid.setHgap(10)
    grid.setVgap(10)
    grid.setPadding(new Insets(25,25,25,25))
    val selectModel = StartWindow.listView.getSelectionModel
    selectModel.selectedItemProperty().addListener(new ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit =
        pathValue(0) = newValue
    })
    btn.setOnAction(new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        if(!nameField.getText.equals("Введите Имя") && !nameField.getText.equals("")){
          Main.setName(nameField.getText)
        }
        if (pathValue(0) == "2551"){
          Main.setPath("2551")
          primaryStage.close()
          Main.setNum("app.conf")

          Platform.setImplicitExit(false)

          startAnotherWindow()
        }else if (pathValue(0) == "2552"){
          Main.setPath("2552")
          primaryStage.close()
          Main.setNum("app1.conf")

          Platform.setImplicitExit(false)

          startAnotherWindow()
        }else{
          Main.setPath("2553")
          primaryStage.close()
          Main.setNum("app2.conf")

          Platform.setImplicitExit(false)

          startAnotherWindow()

        }
      }

    })
    grid.add(nameField,0,0)
    grid.add(listView,0,1)
    grid.add(btn,0,2)


    primaryStage.setTitle("Selection Window")
    primaryStage.centerOnScreen()
    primaryStage.getIcons.add(new Image("icon.png"))
    primaryStage.setScene(scene)
    primaryStage.show

  }

  override def stop(): Unit = {
    System.exit(0)

  }
}
object StartWindow{
  val grid =new GridPane()
  val nameField = new TextArea("Введите Имя")
  val listPath = FXCollections.observableArrayList("2551","2552","2553" )
  val listView = new ListView[String](listPath)
  val pathValue = Array("2551")
  val btn = new Button("ОК")
  val scene = new Scene(grid, 300, 300)

  def startAnotherWindow(): Unit ={
    Main.main(args =null)
    val window = new CommonWindow
    window.newWindow
    Platform.setImplicitExit(true)

  }

}
