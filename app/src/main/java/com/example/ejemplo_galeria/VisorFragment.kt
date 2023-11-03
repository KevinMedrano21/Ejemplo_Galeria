package com.example.ejemplo_galeria

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VisorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VisorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var currentAlbumName: String
    private lateinit var imageView: ImageView
    private lateinit var descriptionTextView : TextView
    private lateinit var albums: List<Album>
    private var currentAlbumIndex = 0
    private var currentImageIndex = 0
    private var albumName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

            val view = inflater.inflate(R.layout.fragment_visor, container, false)

            dbHelper = DatabaseHelper(requireContext())
            imageView = view.findViewById(R.id.imageView)
            descriptionTextView = view.findViewById(R.id.descripcionTextView)
            val spinner = view.findViewById<Spinner>(R.id.spinner)
            val buttonDerecha = view.findViewById<ImageButton>(R.id.buttonDerecha)
            val buttonIzquierda = view.findViewById<ImageButton>(R.id.buttonIzquierda)

            albums = dbHelper.getAllAlbums()
            val albumNames = albums.map { it.nombre }
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, albumNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currentAlbumName = albumNames[position]
                    currentImageIndex = 0
                    mostrarImagenActual()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

            buttonDerecha.setOnClickListener {
                val imagenes = dbHelper.getImagenesByAlbum(currentAlbumName)
                if (currentImageIndex < imagenes.size - 1) {
                    currentImageIndex++
                    mostrarImagenActual()
                }
            }

            buttonIzquierda.setOnClickListener {
                val imagenes = dbHelper.getImagenesByAlbum(currentAlbumName)
                if (currentImageIndex > 0) {
                    currentImageIndex--
                    mostrarImagenActual()
                }
            }
        return view
    }

    private fun mostrarImagenActual() {
        val imagenes = dbHelper.getImagenesByAlbum(currentAlbumName)

        if (imagenes.isNotEmpty() && currentImageIndex in 0 until imagenes.size) {
            val imagen = imagenes[currentImageIndex]
            val imageBytes = imagen.image
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            imageView.setImageBitmap(bitmap)

            val descripcion = imagen.description ?: "Sin descripción"
            descriptionTextView.text = descripcion
        } else {
            // Manejar el caso en el que no hay imágenes en el álbum o el índice es incorrecto
            imageView.setImageResource(android.R.drawable.ic_menu_report_image)
            descriptionTextView.text = "No hay imágenes disponibles"
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VisorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VisorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}