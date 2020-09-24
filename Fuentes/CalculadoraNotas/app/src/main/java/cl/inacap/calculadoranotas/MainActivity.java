package cl.inacap.calculadoranotas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cl.inacap.calculadoranotas.dto.Nota;

public class MainActivity extends AppCompatActivity {

    private int porcentajeActual=0;
    private EditText notaTxt;
    private EditText porcentajeTxt;
    private Button agregarBtn;
    private Button limpiarBtn;
    private ListView notasLv;
    private List<Nota> notas = new ArrayList<>();
    private ArrayAdapter<Nota> adapter;
    private TextView promedioTxt;
    private LinearLayout promedioLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.promedioTxt = findViewById(R.id.promedioTxt);
        this.promedioLl = findViewById(R.id.promedioLl);
        this.notaTxt = findViewById(R.id.notaTxt);
        this.porcentajeTxt = findViewById(R.id.porcentajeTxt);
        this.agregarBtn = findViewById(R.id.agregarBtn);
        this.limpiarBtn = findViewById(R.id.limpiarBtn);
        this.notasLv = findViewById(R.id.notasLv);
        this.adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, notas);
        this.notasLv.setAdapter(adapter);
        this.limpiarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Limpiar los EditText
                notaTxt.setText("");
                porcentajeTxt.setText("");
                //Ocultar Linear Layout
                promedioLl.setVisibility(View.INVISIBLE);
                //Volver porcentaje acumulado a 0
                porcentajeActual=0;
                //Limpiar La Lista
                notas.clear();
                adapter.notifyDataSetChanged();

            }
        });
        this.agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //Lo que tiene que hacer cuando se apreta el boton
                List<String> errores = new ArrayList<>();
                String notaStr= notaTxt.getText().toString().trim();
                String porcStr = porcentajeTxt.getText().toString().trim();
                int porcentaje=0;
                double nota=0;
                try{
                    nota = Double.parseDouble(notaStr);
                    if(nota < 1.0 || nota > 7.0){
                        throw new NumberFormatException();
                    }
                }catch (NumberFormatException ex){
                    errores.add("-La nota debe ser un numero entre 1.0 y 7.0");
                }

                try{
                    porcentaje = Integer.parseInt(porcStr);
                    if(porcentaje <1 || porcentaje >100){
                        throw new NumberFormatException();
                    }
                }catch (NumberFormatException ex){
                    errores.add("-El porcentaje debe ser un numero entre 1 y 100");
                }

                if(errores.isEmpty()){
                    //Ingresar la nota
                    if(porcentaje + porcentajeActual > 100){
                        Toast.makeText(MainActivity.this
                                ,"El porcentaje no puede ser mayor que 100"
                                ,Toast.LENGTH_SHORT).show();
                    }else {
                        Nota n = new Nota();
                        n.setValor(nota);
                        n.setPorcentaje(porcentaje);
                        porcentajeActual += porcentaje;
                        notas.add(n);
                        //Cada vez que modifique el recurso asociado
                        //al adaptador debo llamar a este metodo
                        adapter.notifyDataSetChanged();
                        mostrarPormedio();
                    }
                }else{
                    mostrarErrores(errores);
                }

            }
        });


    }
    private void mostrarPormedio(){
        double promedio=0;
        for(Nota n:notas){
            promedio+= (n.getValor()*(n.getPorcentaje()/100));
        }
        this.promedioTxt.setText(String.format("%.1f",promedio));
        if (promedio < 4.0){
            //Texto sale en rojo
            this.promedioTxt.setTextColor(ContextCompat
                    .getColor(this, R.color.colorError));
        }else{
            //El texto salga en azul
            this.promedioTxt.setTextColor(ContextCompat
                    .getColor(this, R.color.colorExitoso));
        }
        this.promedioLl.setVisibility(View.VISIBLE);
    }


    private void mostrarErrores(List<String> errores){
        //1. Generar una cadena de texto con los errores
        String mensaje="";
        for(String e:errores){
            mensaje+= "-" + e + "\n";
        }
        //2. Mostrar un mensaje de alerta
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder
                .setTitle("Error De Validacion")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar",null)
                .create()
                .show();
    }
}