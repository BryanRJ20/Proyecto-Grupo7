package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import domain.Airport;
import domain.Flight;
import domain.Passenger;
import domain.Status;
import domain.list.DoublyLinkedList;
import domain.list.ListException;
import domain.list.Node;
import domain.list.SinglyLinkedList;
import domain.tree.AVLNode;
import domain.tree.AVLTree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import util.DataLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PassengersController {
    @javafx.fxml.FXML
    private TextField idField;
    @javafx.fxml.FXML
    private TableView tvPassenger;
    @javafx.fxml.FXML
    private TextField nameField;
    @javafx.fxml.FXML
    private TableColumn numberColumn;
    @javafx.fxml.FXML
    private ComboBox nationBox;
    @javafx.fxml.FXML
    private TableColumn originColumn;
    @javafx.fxml.FXML
    private Text txtMessage;
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private TableColumn statusColumn;
    @javafx.fxml.FXML
    private ComboBox passengerBox;
    @javafx.fxml.FXML
    private TableColumn destinationColumn;
    @javafx.fxml.FXML
    private TableColumn dateColumn;

    private AVLTree passengersTree = new AVLTree();
    private SinglyLinkedList flightHistory = new SinglyLinkedList();

    @FXML
    public void initialize() throws ListException, IOException {

        //Cargamos los pasajeros desde json
        loadTree();




        //Carga el ComboBox de naciones
        nationBox.getItems().clear();
        loadNations();

        //Carga combobox de pasajeros
        passengerBox.getItems().clear();

        //Cargar los pasajeros al ComboBox
        loadPassengers(passengerBox);


        numberColumn.setCellValueFactory(new PropertyValueFactory<>("flight number"));
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tvPassenger.setVisible(false);
        tvPassenger.getItems().clear();

    }




    @javafx.fxml.FXML
    public void createPassenger(ActionEvent actionEvent) throws IOException {

        if(validateFields()) {

            if (util.Utility.isNumber(idField.getText())) {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String nationality = String.valueOf(nationBox.getValue());


                Passenger newPassenger = new Passenger(id,name,nationality);

                addAirportToJson(newPassenger);

            }//end isNumber

        }else{util.FXUtility.showErrorAlert("Error", "No passenger has been selected");} //end validateFields

        idField.clear();
        nameField.clear();
        nationBox.getSelectionModel().clearSelection();

        //Vuelve a crear el árbol con el archivo ya modificado
        passengersTree = new AVLTree();
        loadTree();
        tvPassenger.getItems().clear();

    }//end createPassenger


    @FXML
    public void searchPassengerBttn(ActionEvent actionEvent) {

        if(updateFlightHistoryFromSelectedPassenger()){ //Si se encuentra un historial de vuelo para el pasajero seleccionado

            tvPassenger.setVisible(true);
            tvPassenger.getItems().clear();
            tvPassenger.setItems(convertToObservableList(flightHistory)); //Muestra el historial de vuelo de ese pasajero en el table view

        }

        flightHistory = new SinglyLinkedList(); //Limpio la lista una vez utilizada para el siguiente pasajero a buscar

    }

    //Carga el árbol con los pasajeros del JSON
   private void loadTree() throws IOException {passengersTree = DataLoader.loadPassengersFromJson("src/main/resources/ucr/project/passengers.json");}

    //PARA AGREGAR AL JSON
    private void addAirportToJson(Passenger newPassenger) {
        try {
            Gson gson = new Gson();
            //Leer la lista actual de pasajeros desde el archivo
            List<Passenger> passengers;
            try (FileReader reader = new FileReader("src/main/resources/ucr/project/passengers.json")) {
                passengers = gson.fromJson(reader, new TypeToken<List<Passenger>>(){}.getType());
            }

            if (passengers == null) {
                passengers = new ArrayList<>();
            }

            //Agregar el nuevo pasajero a la lista
            passengers.add(newPassenger);

            //Guardar la lista actualizada en el archivo JSON
            try (FileWriter writer = new FileWriter("src/main/resources/ucr/project/passengers.json")) {
                gson.toJson(passengers, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadNations() {
        List<String> nations = Arrays.asList(
                "Costa Rica",
                "Estados Unidos",
                "México",
                "Canadá",
                "España",
                "Francia",
                "Alemania",
                "Brasil",
                "Argentina",
                "Japón",
                "Italia",
                "Reino Unido",
                "Colombia",
                "Chile",
                "Perú",
                "Panamá",
                "Honduras",
                "Guatemala",
                "El Salvador",
                "Nicaragua",
                "Corea del Sur",
                "India",
                "Australia",
                "China",
                "Portugal"
        );


        // Ordena alfabéticamente
        Collections.sort(nations);

        nationBox.getItems().clear();  // Limpia elementos previos
        nationBox.getItems().addAll(nations);  // Agrega la lista
    }



    /**
     * Carga todos los pasajeros almacenados en el árbol AVL al ComboBox
     * @param passengersComboBox El ComboBox donde se cargarán los pasajeros
     */
    private void loadPassengers(ComboBox<Passenger> passengersComboBox) {
        // Verifica si el árbol está vacío
        if (passengersTree == null || passengersTree.isEmpty()) {
            System.out.println("⚠️ No hay pasajeros registrados en el sistema");
            return;
        }

        // Lista para almacenar los pasajeros del árbol
        List<Passenger> passengersList = new ArrayList<>();

        // Obtener todos los pasajeros del árbol AVL (recorrido en orden)
        try {
            fillPassengersList(passengersTree.getRoot(), passengersList);
        } catch (Exception e) {
            System.err.println("❌ Error al cargar pasajeros: " + e.getMessage());
            return;
        }

        //Limpia el ComboBox
        passengersComboBox.getItems().clear();

        // Agrega los pasajeros al ComboBox
        passengersComboBox.getItems().addAll(passengersList);

        // Configura un cell factory para mostrar el nombre y ID del pasajero en el ComboBox
        passengersComboBox.setCellFactory(param -> new ListCell<Passenger>() {
            @Override
            protected void updateItem(Passenger passenger, boolean empty) {
                super.updateItem(passenger, empty);

                if (empty || passenger == null) {
                    setText(null);
                } else {
                    setText(passenger.getName() + " (ID: " + passenger.getId() + ")");
                }
            }
        });

        // También configura cómo se muestra el pasajero seleccionado
        passengersComboBox.setButtonCell(new ListCell<Passenger>() {
            @Override
            protected void updateItem(Passenger passenger, boolean empty) {
                super.updateItem(passenger, empty);

                if (empty || passenger == null) {
                    setText(null);
                } else {
                    setText(passenger.getName() + " (ID: " + passenger.getId() + ")");
                }
            }
        });

        System.out.println("✅ Se cargaron " + passengersList.size() + " pasajeros al ComboBox");

        // Selecciona el primer pasajero si hay elementos
        if (!passengersList.isEmpty()) {
            passengersComboBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * Método auxiliar para realizar un recorrido en orden del árbol AVL
     * y añadir todos los pasajeros a una lista
     * @param node El nodo actual del árbol
     * @param passengersList La lista donde se almacenarán los pasajeros
     */
    private void fillPassengersList(AVLNode node, List<Passenger> passengersList) {
        if (node != null) {
            fillPassengersList(node.left, passengersList);  // Recorre subárbol izquierdo
            passengersList.add((Passenger) node.data);      // Añade el pasajero actual
            fillPassengersList(node.right, passengersList); // Recorre subárbol derecho
        }
    }


    //VALIDAR ESPACIOS
    public boolean validateFields() {
        String id = idField.getText();
        String name = nameField.getText();
        String nationality = String.valueOf(nationBox.getValue());

        if (id == null || id.trim().isEmpty()) {
            util.FXUtility.showErrorAlert("Error", "ID is empty");
            return false;
        }
        if (name == null || name.trim().isEmpty()) {
            util.FXUtility.showErrorAlert("Error", "Name is empty");
            return false;
        }
        if (nationality == null || nationality.trim().isEmpty() || nationBox.getValue() == null) {
            util.FXUtility.showErrorAlert("Error", "No nationality selected");
            return false;
        }
        return true;
    }


    /**
     * Actualiza la lista global flightHistory con los vuelos del pasajero seleccionado en el ComboBox
     * @return true si se encontró historial de vuelos, false si no
     */
    public boolean updateFlightHistoryFromSelectedPassenger() {
        // Obtener el pasajero seleccionado en el ComboBox
        Passenger selectedPassenger = (Passenger) passengerBox.getSelectionModel().getSelectedItem();

        if (selectedPassenger == null) {
            // No hay pasajero seleccionado
            return false;
        }

        // Limpiar la lista global de historial
        flightHistory.clear();

        // Obtener el historial de vuelos del pasajero
        SinglyLinkedList passengerHistory = selectedPassenger.getFlightHistory();

        if (passengerHistory == null || passengerHistory.isEmpty()) {
            // El pasajero no tiene vuelos registrados
            showInfoMessage("NO FLIGHT HISTORY", "The passenger has no flight history");
            return false;
        }

        // Copiar los vuelos del pasajero a la lista global
        Node current = passengerHistory.first;
        while (current != null) {
            flightHistory.add(current.data); // Añade cada vuelo a la lista global
            current = current.next;
        }

        return true;
    }

    //Para mostrar en el table view
    public ObservableList<Flight> convertToObservableList(SinglyLinkedList list) {
        ObservableList<Flight> flightHistory= FXCollections.observableArrayList();
        Node aux = list.first;

        while (aux != null) { // Recorre la lista
            Object data = aux.data;
            Flight current = (Flight) data;
            flightHistory.add(current); // Agrega el el vuelo a la ObservableList
            aux = aux.next; // Avanzamos al siguiente nodo
        } // End while

        return flightHistory;
    }


    // Método auxiliar para mostrar mensajes
    private void showInfoMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Guarda los pasajeros del árbol en el archivo JSON
     */
    private void savePassengersToJson() {
        try {
            // Convertir el árbol a una lista
            List<Passenger> passengersList = new ArrayList<>();
            inOrderTraversal(passengersTree.getRoot(), passengersList);

            // Crear directorio si no existe
            File jsonFile = new File("src/main/resources/ucr/project/passengers.json");
            jsonFile.getParentFile().mkdirs();

            // Guardar usando Gson
            try (FileWriter writer = new FileWriter(jsonFile)) {
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .registerTypeAdapter(SinglyLinkedList.class, new SinglyLinkedListTypeAdapter())
                        .create();

                gson.toJson(passengersList, writer);
            }

            System.out.println("✅ Pasajeros guardados en: " + jsonFile.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("❌ Error al guardar pasajeros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Recorre el árbol en orden para construir una lista
     */
    private void inOrderTraversal(AVLNode node, List<Passenger> list) {
        if (node != null) {
            inOrderTraversal(node.left, list);
            list.add((Passenger)node.data);
            inOrderTraversal(node.right, list);
        }
    }



    /// /////////////////////////////


    /**
     * Adaptador personalizado para serializar/deserializar SinglyLinkedList
     */
    private static class SinglyLinkedListTypeAdapter extends TypeAdapter<SinglyLinkedList> {
        @Override
        public void write(JsonWriter out, SinglyLinkedList list) throws IOException {
            if (list == null || list.isEmpty()) {
                out.nullValue();
                return;
            }

            out.beginArray();
            Node current = list.first;

            while (current != null) {
                Gson gson = new Gson();
                JsonElement element = gson.toJsonTree(current.data);
                gson.toJson(element, out);
                current = current.next;
            }

            out.endArray();
        }

        @Override
        public SinglyLinkedList read(JsonReader in) throws IOException {
            SinglyLinkedList list = new SinglyLinkedList();

            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return list;
            }

            in.beginArray();
            Gson gson = new Gson();

            while (in.hasNext()) {
                // Asumimos que los elementos son Flight
                // Ajusta esto si son de otro tipo
                Flight flight = gson.fromJson(in, Flight.class);
                list.add(flight);
            }

            in.endArray();
            return list;
        }
    }

}
