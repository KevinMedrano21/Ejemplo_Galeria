package com.example.ejemplo_galeria

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity() {

    lateinit var btn1:Button
    lateinit var btn2:Button
    lateinit var fragmentContainer1: FrameLayout
    lateinit var fragmentContainer2: FrameLayout




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn1 = findViewById(R.id.btnImagenes)
        btn2 = findViewById(R.id.btnVisor)

        val initialFragment = ImagenesFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, initialFragment)
            .commit()

        btn1.setOnClickListener {
            // Mostrar ImagenesFragment y ocultar VisorFragment
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, ImagenesFragment()) // Reemplaza ImagenesFragment
            transaction.addToBackStack(null) // Agrega a la pila de retroceso (opcional)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.commit()
        }

        btn2.setOnClickListener {
            // Mostrar VisorFragment y ocultar ImagenesFragment
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, VisorFragment()) // Reemplaza VisorFragment
            transaction.addToBackStack(null) // Agrega a la pila de retroceso (opcional)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.commit()
        }
    }
}

