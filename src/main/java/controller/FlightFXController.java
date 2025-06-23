package controller;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import domain.Airport;
import domain.Flight;
import domain.Passenger;
import domain.Status;
import domain.list.CircularDoublyLinkedList;
import domain.list.DoublyLinkedList;
import domain.list.ListException;
import domain.list.Node;
import domain.tree.AVLTree;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.w3c.dom.css.CSSImportRule;
import util.DataLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlightFXController {
    @javafx.fxml.FXML
    private TableView tvFlights;
    @javafx.fxml.FXML
    private TextField capacityField;
    @javafx.fxml.FXML
    private DatePicker datePicker;
    @javafx.fxml.FXML
    private TableColumn numberColumn;
    @javafx.fxml.FXML
    private TableColumn originColumn;
    @javafx.fxml.FXML
    private TableColumn capacityColumn;
    @javafx.fxml.FXML
    private Text txtMessage;
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private TableColumn statusColumn;
    @javafx.fxml.FXML
    private ComboBox destinationBox;
    @javafx.fxml.FXML
    private TableColumn destinationColumn;
    @javafx.fxml.FXML
    private TableColumn dateColumn;
    @javafx.fxml.FXML
    private ComboBox originBox;
    @javafx.fxml.FXML
    private TableColumn occupancyColumn;
    @FXML
    private Spinner<Integer> hours;
    @FXML
    private Spinner<Integer> minutes;
    @FXML
    private Spinner<Integer> seconds;


    private CircularDoublyLinkedList flightsList = new CircularDoublyLinkedList();
    private AVLTree passengersTree = new AVLTree();

    private int counterID;

    @FXML
    public void initialize() throws ListException, IOException {



        //Cargamos los vuelos desde json
        loadFlightsFromJSON();
        loadTree();


        //Cargas los ComboBox de aeropuertos
        loadAirports();


        // Configuración correcta de las columnas
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("departTime"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        occupancyColumn.setCellValueFactory(new PropertyValueFactory<>("occupancy"));

        // Para la columna Status (que no existe en el JSON)
        statusColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Flight, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Flight, String> cellData) {
                        Flight flight = cellData.getValue();
                        String status = "On Schedule";

                        // Lógica para determinar el estado
                        if (flight.getOccupancy() >= flight.getCapacity()) {
                            status = "Full";
                        } else if (flight.getOccupancy() >= flight.getCapacity() * 0.9) {
                            status = "Almost Full";
                        }

                        return new SimpleStringProperty(status);
                    }
                }
        );

        // Formato para la columna de fecha
        dateColumn.setCellFactory(column -> {
            TableCell<Flight, LocalDateTime> cell = new TableCell<Flight, LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            };
            return cell;
        });

        counterID = 1141;

        tvFlights.getItems().clear();
        tvFlights.setItems(convertToObservableList(flightsList));


        ///  ------------- CONFIGURACION DE HORA Y FECHA ---------------------
        // Configurar el DatePicker con la fecha actual
        datePicker.setValue(LocalDate.now());

        // Configurar los spinners para hora, minuto y segundo
        hours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalDateTime.now().getHour()));
        minutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalDateTime.now().getMinute()));
        seconds.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalDateTime.now().getSecond()));

        // Opcionalmente permitir edición manual de los spinners
        hours.setEditable(true);
        minutes.setEditable(true);
        seconds.setEditable(true);

        // Listener para actualizar el resultado cuando cambien los valores
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateDateTime2());
        hours.valueProperty().addListener((obs, oldVal, newVal) -> updateDateTime2());
        minutes.valueProperty().addListener((obs, oldVal, newVal) -> updateDateTime2());
        seconds.valueProperty().addListener((obs, oldVal, newVal) -> updateDateTime2());

        // Inicializar con la fecha y hora actual
        updateDateTime2();

        // Establecer un conversor de cadena para el DatePicker
        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty())
                        ? LocalDate.parse(string, dateFormatter) : null;
            }
        });

        ///  ------------------------------------------------------------------------------------
        // Esto fuerza a actualizar el valor cuando el usuario hace clic en una fecha
        datePicker.setOnAction(event -> {
            LocalDate selectedDate = datePicker.getValue();
            System.out.println("Fecha seleccionada manualmente: " + selectedDate);

            // Forzar la actualización del modelo
            if (selectedDate != null) {
                datePicker.setValue(selectedDate);
            }
        });

        // Añadir también manejo para cuando se introduce texto manualmente
        datePicker.getEditor().setOnAction(event -> {
            try {
                String text = datePicker.getEditor().getText();
                LocalDate parsedDate = LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                datePicker.setValue(parsedDate);
                System.out.println("Fecha ingresada manualmente: " + parsedDate);
            } catch (Exception e) {
                System.out.println("Error al parsear fecha ingresada: " + e.getMessage());
            }
        });

    }

    //Carga el árbol con los pasajeros del JSON
    private void loadTree() throws IOException {passengersTree = DataLoader.loadPassengersFromJson("src/main/resources/ucr/project/passengers.json");}


    @javafx.fxml.FXML
    public void assignPassengersAction(ActionEvent actionEvent) {

        Flight selectedFlight= (Flight) tvFlights.getSelectionModel().getSelectedItem();

        if (selectedFlight != null) {

            assignRandomPassengersToFlights(selectedFlight);

        }else {util.FXUtility.showErrorAlert("Error", "No flight has been selected");}

    }


    @javafx.fxml.FXML
    public void createFlight(ActionEvent actionEvent) {

        if(validateFields()) {
            if (util.Utility.isNumber(capacityField.getText())) {
                int capacity = Integer.parseInt(capacityField.getText());
                LocalDateTime dateTime = updateDateTime2();
                String origin = String.valueOf(originBox.getValue());
                String destination = String.valueOf(destinationBox.getValue());

                if(util.Utility.compare(origin, destination)==0){

                    showInfoMessage("Erorr", "Origin and destination are the same");
                }else { //Lo crea
                    Flight newFlight = new Flight(counterID++, origin, destination, dateTime, capacity);
                    addFlightToJson(newFlight);
                }
            }//end isNumber

        }else{util.FXUtility.showErrorAlert("Error", "Please fill all the fields");} //end validateFields

        // Limpiar campos de texto y selección
        capacityField.clear();
        originBox.getSelectionModel().clearSelection();
        destinationBox.getSelectionModel().clearSelection();

        // Limpiar DatePicker (establecer a null)
        datePicker.setValue(null);

        // Resetear spinners de tiempo a 0 o valores predeterminados
        hours.getValueFactory().setValue(0);
        minutes.getValueFactory().setValue(0);
        seconds.getValueFactory().setValue(0);


        //Vuelve a crear la lista con el archivo ya modificado
        flightsList = new CircularDoublyLinkedList();
        loadFlightsFromJSON();
        tvFlights.getItems().clear();
        tvFlights.setItems(convertToObservableList(flightsList));

    }


    /**
     * Asigna pasajeros aleatorios a vuelos
     */
    public void assignRandomPassengersToFlights(Flight flight) {
        try {
            if (flightsList.isEmpty()) {
                showInfoMessage("Alert", "⚠️ No flights available for passenger assignment");
                return;
            }

            System.out.println("🎫 Assigning passengers to flights...");

                assignPassengersToFlight(flight);


           showInfoMessage("Success","✅ Passengers assigned to flights successfully" );
        } catch (Exception e) {
            showInfoMessage("Error","❌ Error assigning passengers to flights: " + e.getMessage());
        }
    }

    /**
     * Asigna pasajeros a un vuelo específico
     */
    private void assignPassengersToFlight(Flight flight) {
        int currentOccupancy = flight.getOccupancy();

        for (int i = 0; i < currentOccupancy; i++) {
            // Generar ID de pasajero aleatorio
            int passengerId = 10000 + util.Utility.random(0001,9999);
            Passenger passenger = passengersTree.search(passengerId);

            if (passenger != null) {
                passenger.addFlightToHistory(flight);
            }
        }
    }

    public void loadFlightsFromJSON() {
        //Carga la lista desde JSON a la flightsList
        try (FileReader reader = new FileReader("src/main/resources/ucr/project/flights.json")) {
            // Crear un adaptador usando el formato ISO 8601
            GsonBuilder gsonBuilder = new GsonBuilder();

            // Registrar adaptador para LocalDateTime
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                        throws JsonParseException {
                    return LocalDateTime.parse(json.getAsString());
                }
            });

            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.toString());
                }
            });

            Gson gson = gsonBuilder.create();

            List<Flight> flights = gson.fromJson(reader, new TypeToken<List<Flight>>() {
            }.getType());

            if (flightsList == null) {
                flightsList = new CircularDoublyLinkedList();
            } else {
                flightsList.clear();
            }

            for (Flight flight : flights) {
                flightsList.add(flight); //Lo añade a la lista circular
            }

            System.out.println("Vuelos cargados: " + flightsList.size());

        } catch (Exception e) {
            System.err.println("Error al cargar vuelos: " + e.getMessage());
            e.printStackTrace();
        }
    }//end load from json

    // Adaptado para CircularLinkedList
    public ObservableList<Flight> convertToObservableList(CircularDoublyLinkedList list) {
        ObservableList<Flight> flights = FXCollections.observableArrayList();

        if (list.isEmpty()) {
            return flights;
        }

        Node aux = list.first;
        do {
            // Procesar el nodo actual
            Flight current = (Flight) aux.data;
            flights.add(current);
            aux = aux.next;
        } while (aux != list.first); // Se detiene al volver al inicio

        return flights;
    }

    private void addFlightToJson(Flight newFlight) {
        try {
            Gson gson = new Gson();
            //Leer la lista actual de vuelo desde el archivo
            List<Flight> flights;
            try (FileReader reader = new FileReader("src/main/resources/ucr/project/flights.json")) {
                flights = gson.fromJson(reader, new TypeToken<List<Flight>>(){}.getType());
            }

            if (flights == null) {
                flights = new ArrayList<>();
            }

            //Agregar el nuevo vuelo a la lista
            flights.add(newFlight);

            //Guardar la lista actualizada en el archivo JSON
            try (FileWriter writer = new FileWriter("src/main/resources/ucr/project/flights.json")) {
                gson.toJson(flights, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadAirports() {
        List<String> airportNames = Arrays.asList(
                "Aeropuerto Internacional Juan Santamaría",
                "John F. Kennedy International Airport",
                "London Heathrow Airport",
                "Charles de Gaulle Airport",
                "Tokyo Haneda Airport",
                "São Paulo–Guarulhos International Airport",
                "Sydney Kingsford Smith Airport",
                "Frankfurt am Main Airport",
                "Dubai International Airport",
                "Toronto Pearson International Airport",
                "Changi Airport",
                "Incheon International Airport",
                "Amsterdam Schiphol Airport",
                "Madrid-Barajas Adolfo Suárez Airport",
                "Beijing Capital International Airport",
                "O. R. Tambo International Airport",
                "Los Angeles International Airport",
                "Munich Airport",
                "Zurich Airport",
                "Benito Juárez International Airport"
        );

        // Orden alfabético
        Collections.sort(airportNames);

        // Configurar originBox
        originBox.getItems().clear();
        originBox.getItems().addAll(airportNames);

        // Configurar destinationBox
        destinationBox.getItems().clear();
        destinationBox.getItems().addAll(airportNames);


    }//end loadAirports


    public boolean validateFields() {
        String origin = String.valueOf(originBox.getValue());
        String destination = String.valueOf(destinationBox.getValue());
        String date = String.valueOf(datePicker.getValue());
        String capacity = capacityField.getText();

        if (capacity == null || capacity.trim().isEmpty()) {
            util.FXUtility.showErrorAlert("Error", "Capacity is empty");
            return false;
        }
        if (date == null || date.trim().isEmpty() || datePicker.getValue() == null) {
            util.FXUtility.showErrorAlert("Error", "Date is empty");
            return false;
        }
        if (origin == null || origin.trim().isEmpty() || originBox.getValue() == null) {
            util.FXUtility.showErrorAlert("Error", "No origin selected");
            return false;
        }
        if (destination == null || destination.trim().isEmpty() || destinationBox.getValue() == null) {
            util.FXUtility.showErrorAlert("Error", "No destination selected");
            return false;
        }

        return true;
    }

    private LocalDateTime updateDateTime2() {
        LocalDate date = getSelectedDate();

        // Ahora date nunca será null, pero podemos mostrar mensaje si fue necesario usar fecha por defecto
        if (date.equals(LocalDate.now()) && datePicker.getValue() == null) {
            showInfoMessage("Fecha por defecto", "Se está usando la fecha actual porque no se detectó selección");
        }

        int hour = hours.getValue() != null ? hours.getValue() : 0;
        int minute = minutes.getValue() != null ? minutes.getValue() : 0;
        int second = seconds.getValue() != null ? seconds.getValue() : 0;

        // Crear el objeto LocalDateTime y formatearlo
        LocalDateTime dateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                hour, minute, second);

        return dateTime;
    }


    private LocalDateTime updateDateTime() {
        LocalDate date = datePicker.getValue();
        if (date == null) {
          showInfoMessage("No Date", "Please select a date");
            return null; // Retornar null para evitar el NullPointerException
        }

        int hour = hours.getValue();
        int minute = minutes.getValue();
        int second = seconds.getValue();

        // Crear el objeto LocalDateTime y formatearlo
        LocalDateTime dateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                hour, minute, second);

        // Formatear según el formato deseado "2025-06-20T14:45:00"
        //String formattedDateTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Mostrar el resultado
        return dateTime;
    }

    @FXML
    public String getFormattedDateTime() {
        // Método para obtener el valor formateado cuando sea necesario
        LocalDate date = datePicker.getValue();
        if (date == null) return null;

        LocalDateTime dateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                hours.getValue(),
                minutes.getValue(),
                seconds.getValue());

        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private LocalDate getSelectedDate() {
        // Intento 1: método normal
        LocalDate date = datePicker.getValue();

        // Intento 2: obtener desde el editor si el método normal falla
        if (date == null && datePicker.getEditor() != null) {
            try {
                String text = datePicker.getEditor().getText();
                if (text != null && !text.isEmpty()) {
                    // Intenta parsear con varios formatos comunes
                    try {
                        date = LocalDate.parse(text);
                    } catch (Exception e1) {
                        try {
                            date = LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        } catch (Exception e2) {
                            try {
                                date = LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            } catch (Exception e3) {
                                // Otros formatos si es necesario
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Intento 3: Si todo falla, se usa la fecha actual
        if (date == null) {
            System.out.println("No se pudo obtener fecha, usando la fecha actual");
            date = LocalDate.now();
        }

        return date;
    }

    // Método auxiliar para mostrar mensajes
    private void showInfoMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

