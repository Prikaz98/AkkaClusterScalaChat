package model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.{FXCollections, ObservableList}
import model.ChatModel.commonRoom

class ChatModel {
  val newMessage: SimpleStringProperty = new SimpleStringProperty()
  val users: ObservableList[String] = FXCollections.observableArrayList(commonRoom)
}

object ChatModel {
  val commonRoom : String = "Common room"
}
