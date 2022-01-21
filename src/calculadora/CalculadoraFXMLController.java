package calculadora;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class para el archivo CalculadoraFXML.fxml.
 *
 * @author Radomir Dimitrov Atanasov
 */
public class CalculadoraFXMLController implements Initializable {

    /*----------------------------------------------------------------------*
     *  VARIABLES AUXILIARES (NO FXML)
     *----------------------------------------------------------------------*/
    //ENUM QUE REPRESENTA LAS OPERACIONES QUE PRACTICAREMOS
    private enum Operaciones {
        SUMAR, RESTAR, MULTIPLICAR, DIVIDIR
    };

    //VARIABLES FINALES (CONSTANTES)
    private static final int MAX_DIGITOS = 16; //MÁXIMO DE DIGITOS DEL OPERANDO

    //VARIABLES CON LSA QUE TRABAJAMOS LA CALCULADORA
    private Double operando1; //Operando 1
    private Double operando2; //Operando 2
    private Double resultado; //Resultado de la operacion
    private int digitos = 0; //Digitos acumulados del operando
    private Operaciones operacion = null; //Operacion que se va a realizar
    private boolean operacionRealizada = false; //Si se ha realizado o no la operacion
    private boolean comaActivada = false; //Si la coma ha sido pulsada o no
    private boolean onOff = false; //Si el boton ON/OFF ha sido pulsado
    private boolean horaOn = false; //Si el boton HORA ha sido pulsado
    private int botonSecretoPulsado = 0; //Si el boton secreto ha sido pulsado

    //Estilos que tienen lso botones (pueden ir cambiando si se pulsa el boton secreto)
    private static String BOTON_LIGHTGREY
            = "-fx-background-color: lightgrey";
    private static String BOTON_DARKGREY
            = "-fx-background-color: darkgrey";
    private static String BOTON_ORANGE
            = "-fx-background-color: orange";
    private static String BOTON_ENTERED
            = "-fx-border-color: black;"
            + "-fx-border-width: 1";

    /*----------------------------------------------------------------------*
     *  VARIABLES FXML
     *----------------------------------------------------------------------*/
    @FXML //BOTONES
    Button boton1, boton2, boton3, boton4, boton5,
            boton6, boton7, boton8, boton9, boton0,
            botonSuma, botonResta, botonMult, botonDiv,
            botonC, botonIgual, botonMasMenos, botonComa,
            botonPi, botonE, botonCos, botonSin, botonTan,
            botonSecreto, botonOnOff, botonHora;
    @FXML //DISPLAY POR EL QUE SE IMPRIMEN LOS PERANDOS Y RESLUTADOS
    Label display;

    @FXML //PANE DONDE SE UBICAN LOS BOTONES Y EL DISPLAY
    Pane pane;

    /*----------------------------------------------------------------------*
     *  MÉTODOS FXML
     *----------------------------------------------------------------------*/
    /**
     * Botón en el menú FILE -> sirve para salir de la aplicacion.
     */
    @FXML
    private void menuClose() {
        System.exit(0);
    }

    /**
     * Botón en el menú HELP -> sirve para abrir una ventana emergente en la que
     * aparece información sobre la calculadora.
     */
    @FXML
    private void menuAbout() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(20);

        dialogVbox.setStyle("-fx-background-color: black");
        Label label1 = new Label();
        Label label2 = new Label();
        Label label3 = new Label();
        Label label4 = new Label();
        Label label5 = new Label();

        label1.setText("Calculadora realizada por");
        label1.setStyle("-fx-text-fill: white");
        label2.setText("Radomir Dimitrov Atanasov");
        label2.setStyle("-fx-text-fill: orange");
        label3.setText("Tarea para la unidad 10 del");
        label3.setStyle("-fx-text-fill: white");
        label4.setText("módulo profesional PROGRAMACIÓN");
        label4.setStyle("-fx-text-fill: white");
        label5.setText("Versión: 1.0");
        label5.setStyle("-fx-text-fill: orange");

        label2.setUnderline(true);
        label5.setUnderline(true);

        label1.setMaxWidth(Double.MAX_VALUE);
        label2.setMaxWidth(Double.MAX_VALUE);
        label3.setMaxWidth(Double.MAX_VALUE);
        label4.setMaxWidth(Double.MAX_VALUE);
        label5.setMaxWidth(Double.MAX_VALUE);

        label1.setAlignment(Pos.CENTER);
        label2.setAlignment(Pos.CENTER);
        label3.setAlignment(Pos.CENTER);
        label4.setAlignment(Pos.CENTER);
        label5.setAlignment(Pos.CENTER);

        dialogVbox.getChildren().addAll(label1, label2, label3, label4, label5);
        Scene dialogScene = new Scene(dialogVbox, 300, 200);

        dialog.setScene(dialogScene);
        dialog.show();
        dialog.setResizable(false);
    }//fin menuAbout

    /*----------------------------------------------------------------------*
     * Los siguientes métodos cambian el estilo de los botones sobre los que
     * actuan al detectar el raton entrar o salir del boton.
     *----------------------------------------------------------------------*/
    @FXML
    private void mouseEnteredLightgrey(MouseEvent event) {
        ((Button) event.getSource()).setStyle(BOTON_ENTERED);
    }

    @FXML
    private void mouseExitedLightgrey(MouseEvent event) {
        ((Button) event.getSource()).setStyle(BOTON_LIGHTGREY);
    }

    @FXML
    private void mouseEnteredDarkgrey(MouseEvent event) {
        ((Button) event.getSource()).setStyle(BOTON_ENTERED);
    }

    @FXML
    private void mouseExitedDarkgrey(MouseEvent event) {
        ((Button) event.getSource()).setStyle(BOTON_DARKGREY);
    }

    @FXML
    private void mouseEnteredOrange(MouseEvent event) {
        ((Button) event.getSource()).setStyle(BOTON_ENTERED);
    }

    @FXML
    private void mouseExitedOrange(MouseEvent event) {
        ((Button) event.getSource()).setStyle(BOTON_ORANGE);
    }

    /**
     * Método que maneja lo que se hace al pulsar alguno de los botones que hay
     * en la calculadora. El método averigua que boton ha sido pulsado y en
     * funcion de esto llama a uno u otro metodo auxiliar para manejar la accion
     * correspondiente
     *
     * @param event
     */
    @FXML
    private void handleButtonAction(ActionEvent event) {

        if (event.getSource() == boton1) {
            manejar1();
        } else if (event.getSource() == boton2) {
            manejar2();
        } else if (event.getSource() == boton3) {
            manejar3();
        } else if (event.getSource() == boton4) {
            manejar4();
        } else if (event.getSource() == boton5) {
            manejar5();
        } else if (event.getSource() == boton6) {
            manejar6();
        } else if (event.getSource() == boton7) {
            manejar7();
        } else if (event.getSource() == boton8) {
            manejar8();
        } else if (event.getSource() == boton9) {
            manejar9();
        } else if (event.getSource() == boton0) {
            manejar0();
        } else if (event.getSource() == botonC) {
            manejarC();
        } else if (event.getSource() == botonSuma) {
            manejarSuma();
        } else if (event.getSource() == botonResta) {
            manejarResta();
        } else if (event.getSource() == botonMult) {
            manejarMult();
        } else if (event.getSource() == botonDiv) {
            manejarDiv();
        } else if (event.getSource() == botonIgual) {
            manejarIgual();
        } else if (event.getSource() == botonMasMenos) {
            manejarMasMenos();
        } else if (event.getSource() == botonComa) {
            manejarComa();
        } else if (event.getSource() == botonPi) {
            manejarPi();
        } else if (event.getSource() == botonE) {
            manejarE();
        } else if (event.getSource() == botonCos) {
            manejarCos();
        } else if (event.getSource() == botonSin) {
            manejarSin();
        } else if (event.getSource() == botonTan) {
            manejarTan();
        } else if (event.getSource() == botonSecreto) {
            manejarBotonSecreto();
        } else if (event.getSource() == botonOnOff) {
            manejarBotonOnOff();
        } else if (event.getSource() == botonHora) {
            manejarBotonHora();
        }
    }//fin handleButtonAction

    /*------------------------------------------------------------------------*
     * A CONTINUACION APARECEN LSO MÉTODOS AUXILIARES PARA COMPLETAR LAS TAREAS
     *------------------------------------------------------------------------*
     *------------------------------------------------------------------------*
     * METODOS QUE MANEJAN LOS BOTONES NUMERICOS (DE 0 A 9)
     *------------------------------------------------------------------------*/
    private void manejar1() {
        if (digitos == 0) {
            display.setText("");
        }
        if (digitos < MAX_DIGITOS) {
            display.setText(display.getText() + "1");
            digitos++;
        }
        operacionRealizada = false;
    }//fin manejar 1

    private void manejar2() {
        if (digitos == 0) {
            display.setText("");
        }
        if (digitos < MAX_DIGITOS) {
            display.setText(display.getText() + "2");
            digitos++;
        }
        operacionRealizada = false;
    }//fin manejar 2

    private void manejar3() {
        if (digitos == 0) {
            display.setText("");
        }
        if (digitos < MAX_DIGITOS) {
            display.setText(display.getText() + "3");
            digitos++;
        }
        operacionRealizada = false;
    }//fin manejar3

    private void manejar4() {
        if (digitos == 0) {
            display.setText("");
        }
        if (digitos < MAX_DIGITOS) {
            display.setText(display.getText() + "4");
            digitos++;
        }
        operacionRealizada = false;
    }//fin manejar4

    private void manejar5() {
        if (digitos == 0) {
            display.setText("");
        }
        if (digitos < MAX_DIGITOS) {
            display.setText(display.getText() + "5");
            digitos++;
        }
        operacionRealizada = false;
    }//fin manejar5

    private void manejar6() {
        if (digitos == 0) {
            display.setText("");
        }
        if (digitos < MAX_DIGITOS) {
            display.setText(display.getText() + "6");
            digitos++;
        }
        operacionRealizada = false;
    }//fin manejar6

    private void manejar7() {
        if (digitos == 0) {
            display.setText("");
        }
        if (digitos < MAX_DIGITOS) {
            display.setText(display.getText() + "7");
            digitos++;
        }
        operacionRealizada = false;
    }//fin manejar7

    private void manejar8() {
        if (digitos == 0) {
            display.setText("");
        }
        if (digitos < MAX_DIGITOS) {
            display.setText(display.getText() + "8");
            digitos++;
        }
        operacionRealizada = false;
    }//fin manejar8

    private void manejar9() {
        if (digitos == 0) {
            display.setText("");
        }
        if (digitos < MAX_DIGITOS) {
            display.setText(display.getText() + "9");
            digitos++;
        }
        operacionRealizada = false;
    }//fin manejar9

    private void manejar0() {
        if (digitos == 0) {
            display.setText("0");
        } else if (digitos < MAX_DIGITOS) {
            display.setText(display.getText() + "0");
            digitos++;
        }
        operacionRealizada = false;
    }//fin manejar0

    /*----------------------------------------------------------------------*
     * METODOS QUE MANEJAN LSO BOTONES: PI, E, COS, SIN, TAN
     *----------------------------------------------------------------------*/
    private void manejarPi() {
        display.setText(String.valueOf(Math.PI));
        digitos = 0;
        operacionRealizada = false;
    }//fin manejarPi

    private void manejarE() {
        display.setText(String.valueOf(Math.E));
        digitos = 0;
        operacionRealizada = false;
    }//fin manejarE

    private void manejarCos() {
        Double aux = Double.valueOf(display.getText());
        display.setText(quitarCerosRestantes(String.valueOf(Math.cos(aux))));
        digitos = 0;
        operacionRealizada = false;
    }//fin manejarCos

    private void manejarSin() {
        Double aux = Double.valueOf(display.getText());
        display.setText(quitarCerosRestantes(String.valueOf(Math.sin(aux))));
        digitos = 0;
        operacionRealizada = false;
    }//fin ManejarSin

    private void manejarTan() {
        Double aux = Double.valueOf(display.getText());
        display.setText(quitarCerosRestantes(String.valueOf(Math.tan(aux))));
        digitos = 0;
        operacionRealizada = false;
    }//fin manejarTan

    /*----------------------------------------------------------------------*
     * METODOS QUE MANEJAN OTROS BOTONES
     *----------------------------------------------------------------------*/
    /**
     * Boton secreto.
     *
     * Cambia el estilo de colores de la calculadora. Consta de 3 estilos.
     */
    private void manejarBotonSecreto() {
        switch (botonSecretoPulsado) {
            //Primer estilo
            case 0:
                BOTON_LIGHTGREY
                        = "-fx-background-color: lightblue";
                BOTON_DARKGREY
                        = "-fx-background-color: darkblue;"
                        + "-fx-text-fill: white";
                BOTON_ORANGE
                        = "-fx-background-color: coral";
                BOTON_ENTERED
                        = "-fx-border-color: blue;"
                        + "-fx-border-width: 1";
                botonSecretoPulsado = 1;
                boton1.setText("Uno");
                boton2.setText("Dos");
                boton3.setText("Tres");
                boton4.setText("Cuatro");
                boton5.setText("Cinco");
                boton6.setText("Seis");
                boton7.setText("Siete");
                boton8.setText("Ocho");
                boton9.setText("Nueve");
                boton0.setText("Cero");
                break;
            //Segundo estilo
            case 1:
                BOTON_LIGHTGREY
                        = "-fx-background-color: lightgreen";
                BOTON_DARKGREY
                        = "-fx-background-color: darkgreen;"
                        + "-fx-text-fill: white";
                BOTON_ORANGE
                        = "-fx-background-color: red;"
                        + "-fx-text-fill: white";
                BOTON_ENTERED
                        = "-fx-border-color: black;"
                        + "-fx-border-width: 1";
                boton1.setText("One");
                boton2.setText("Two");
                boton3.setText("Three");
                boton4.setText("Four");
                boton5.setText("Five");
                boton6.setText("Six");
                boton7.setText("Seven");
                boton8.setText("Eight");
                boton9.setText("Nine");
                boton0.setText("Zero");
                botonSecretoPulsado = 2;
                break;
            //Tercer estilo
            case 2:
                BOTON_LIGHTGREY
                        = "-fx-background-color: lightgrey";
                BOTON_DARKGREY
                        = "-fx-background-color: darkgrey";
                BOTON_ORANGE
                        = "-fx-background-color: orange";
                BOTON_ENTERED
                        = "-fx-border-color: black;"
                        + "-fx-border-width: 1";
                boton1.setText("1");
                boton2.setText("2");
                boton3.setText("3");
                boton4.setText("4");
                boton5.setText("5");
                boton6.setText("6");
                boton7.setText("7");
                boton8.setText("8");
                boton9.setText("9");
                boton0.setText("0");
                botonSecretoPulsado = 0;
                break;
            default:
                break;
        }//fin switch
    }//fin manejarbotonSecreto

    /**
     * Inhabilita y habilita la posibilidad de utuilizar los siguientes 5
     * botones: Cos, Sin, Tan, E, Pi.
     */
    private void manejarBotonOnOff() {

        //INHABILITA
        if (onOff == false) {
            botonCos.setDisable(true);
            botonSin.setDisable(true);
            botonTan.setDisable(true);
            botonE.setDisable(true);
            botonPi.setDisable(true);
            onOff = true;
        } else {
            //HABILITA
            botonCos.setDisable(false);
            botonSin.setDisable(false);
            botonTan.setDisable(false);
            botonE.setDisable(false);
            botonPi.setDisable(false);
            onOff = false;
        }
    }//fin manejarBotonOnOff

    /**
     * Imprime la fecha y hora actual por pantalla.
     *
     * Mientras aparece la hora los demás botones están ihabilitados.
     */
    private void manejarBotonHora() {
        //Poner hora y desactivar botones
        if (horaOn == false) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy ', ' HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            display.setText(formatter.format(date));
            boton1.setDisable(true);
            boton2.setDisable(true);
            boton3.setDisable(true);
            boton4.setDisable(true);
            boton5.setDisable(true);
            boton6.setDisable(true);
            boton7.setDisable(true);
            boton8.setDisable(true);
            boton9.setDisable(true);
            boton0.setDisable(true);
            botonSuma.setDisable(true);
            botonResta.setDisable(true);
            botonMult.setDisable(true);
            botonDiv.setDisable(true);
            botonC.setDisable(true);
            botonIgual.setDisable(true);
            botonMasMenos.setDisable(true);
            botonComa.setDisable(true);
            botonPi.setDisable(true);
            botonE.setDisable(true);
            botonCos.setDisable(true);
            botonSin.setDisable(true);
            botonTan.setDisable(true);
            botonSecreto.setDisable(true);
            botonOnOff.setDisable(true);
            horaOn = true; //Indica que la hora está activa
        } else {
            //Quitar hora y volver a habilitar botones
            manejarC();
            boton1.setDisable(false);
            boton2.setDisable(false);
            boton3.setDisable(false);
            boton4.setDisable(false);
            boton5.setDisable(false);
            boton6.setDisable(false);
            boton7.setDisable(false);
            boton8.setDisable(false);
            boton9.setDisable(false);
            boton0.setDisable(false);
            botonSuma.setDisable(false);
            botonResta.setDisable(false);
            botonMult.setDisable(false);
            botonDiv.setDisable(false);
            botonC.setDisable(false);
            botonIgual.setDisable(false);
            botonMasMenos.setDisable(false);
            botonComa.setDisable(false);
            if (onOff == false) {
                botonPi.setDisable(false);
                botonE.setDisable(false);
                botonCos.setDisable(false);
                botonSin.setDisable(false);
                botonTan.setDisable(false);
            }
            botonOnOff.setDisable(false);
            botonSecreto.setDisable(false);
            horaOn = false; //Indica que la hora no está activa
        }
    }//fin manejarBotonhora

    /*----------------------------------------------------------------------*
     * METODOS QUE MANEJAN BOTONES AUXILIARES: C (ANULAR), +- (SIGNO), COMA
     *----------------------------------------------------------------------*/
    private void manejarC() {
        digitos = 0;
        display.setText("0");
        operando1 = Double.valueOf(0);
        operando2 = Double.valueOf(0);
        resultado = Double.valueOf(0);
        operacionRealizada = false;
        operacion = null;
        comaActivada = false;
    }//fin manejarC

    private void manejarMasMenos() {
        Double aux = Double.valueOf(display.getText());
        if (aux != 0) {
            aux *= -1;
            display.setText(quitarCerosRestantes(String.valueOf(aux)));
        }
    }//fin manejarMasmenos

    private void manejarComa() {
        if (comaActivada == false) {
            if (digitos != 0) {
                display.setText(display.getText() + ".");
                digitos++;
            } else {
                display.setText("0.");
                digitos += 2;
            }
            comaActivada = true;
        }
    }//fin manejarComa

    /**
     * Desaparece los 0s al final.
     *
     * @param s
     * @return
     */
    private String quitarCerosRestantes(String s) {
        return !s.contains(".") ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
    }//fin quitarCerosRestantes

    /*----------------------------------------------------------------------
     * METODOS QUE MANEJAN LAS OPERACIONES: SUMA, RESTA, MULTIPLICACION, 
     * DIVISION Y IGUAL.
     *
     * CADA UNO DE ELLOS LLAMA AL METODO realizarOperacion() PARA PROCESAR
     * LA OPERACION REQUERIDA.
     *----------------------------------------------------------------------*/
    private void manejarSuma() {
        if (operacion == null) {
            operando1 = Double.parseDouble(display.getText());
            operacionRealizada = true;
        } else if (operacionRealizada == false) {
            realizarOperacion();
        }
        operacion = Operaciones.SUMAR;
        digitos = 0;
        comaActivada = false;
    }//fin manejarSuma

    private void manejarResta() {
        if (operacion == null) {
            operando1 = Double.parseDouble(display.getText());
            operacionRealizada = true;
        } else if (operacionRealizada == false) {
            realizarOperacion();
        }
        operacion = Operaciones.RESTAR;
        digitos = 0;
        comaActivada = false;
    }//fin manejarResta

    private void manejarMult() {
        if (operacion == null) {
            operando1 = Double.parseDouble(display.getText());
            operacionRealizada = true;
        } else if (operacionRealizada == false) {
            realizarOperacion();
        }
        operacion = Operaciones.MULTIPLICAR;
        digitos = 0;
        comaActivada = false;
    }//fin manejarMult

    private void manejarDiv() {
        if (operacion == null) {
            operando1 = Double.parseDouble(display.getText());
            operacionRealizada = true;
        } else if (operacionRealizada == false) {
            realizarOperacion();
        }
        operacion = Operaciones.DIVIDIR;
        digitos = 0;
        comaActivada = false;
    }//fin manejarDiv

    private void manejarIgual() {
        if (operacion != null) {
            realizarOperacion();
        }
        digitos = 0;
        operando1 = Double.valueOf(0);
        operando2 = Double.valueOf(0);
        resultado = Double.valueOf(0);
        operacionRealizada = false;
        operacion = null;
        comaActivada = false;
    }//fin manejarIgual

    /**
     * Realizar la operacion segun si es Suma, Resta, Multiplicacion, Division y
     * mostrarla por pantalla.
     */
    private void realizarOperacion() {
        operando2 = Double.parseDouble(display.getText());
        switch (operacion) {
            case SUMAR:
                resultado = operando1 + operando2;
                display.setText(quitarCerosRestantes(String.valueOf(resultado)));
                break;
            case RESTAR:
                resultado = operando1 - operando2;
                display.setText(quitarCerosRestantes(String.valueOf(resultado)));
                break;
            case MULTIPLICAR:
                resultado = operando1 * operando2;
                display.setText(quitarCerosRestantes(String.valueOf(resultado)));
                break;
            case DIVIDIR: {
                try {
                    resultado = operando1 / operando2;
                    if (resultado == Double.POSITIVE_INFINITY
                            || resultado == Double.NEGATIVE_INFINITY
                            || resultado == Double.NaN) {
                        display.setText("NaN");
                    } else {
                        display.setText(quitarCerosRestantes(String.valueOf(resultado)));
                    }
                } catch (Exception e) {
                    display.setText("NaN");
                }
                break;
            }
        }//fin switch
        operando1 = resultado;
        operacionRealizada = true;
    }//fin realizarOperacion

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Inicializar valores de variables que necesitamos
        display.setText("0");
        digitos = 0;
        operacion = null;
        operacionRealizada = false;
        comaActivada = false;
        onOff = false;
        horaOn = false;
        botonSecretoPulsado = 0;

    }//fin initialize

}//fin CalculadoraFXMLController
