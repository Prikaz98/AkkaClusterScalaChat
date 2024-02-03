package model

import javafx.beans.property.SimpleStringProperty
import javafx.collections.{FXCollections, ObservableList}

class ChatModel {
  val newMessage: SimpleStringProperty = new SimpleStringProperty()
  val users: ObservableList[String] = FXCollections.observableArrayList("Common room")
}
