package com.example.ejemplo_galeria

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.chip.Chip




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImagenesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImagenesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var imageView: ImageView // Agregar esta línea
    private val REQUEST_IMAGE_PICK = 100
    private var selectedImageUri: Uri? = null
    private lateinit var selectedAlbumName:String

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            // Se seleccionó una imagen de la galería
            selectedImageUri = data?.data
            imageView.setImageURI(selectedImageUri)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        dbHelper = DatabaseHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_imagenes, container, false)
        val chipAlbum = view.findViewById<Chip>(R.id.chipAlbum)
        imageView = view.findViewById<ImageView>(R.id.txtView1)
        val editText = view.findViewById<EditText>(R.id.editText1)
        val buttonGuardar = view.findViewById<Button>(R.id.buttonGuardar)
        val spinnerImagenes = view.findViewById<Spinner>(R.id.spinnerImagenes)

        val albumList = dbHelper.getAllAlbums()
        val albumNames = albumList.map { it.nombre }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, albumNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerImagenes.adapter = adapter

        spinnerImagenes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedAlbumName = albumNames[position]

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


        chipAlbum.setOnClickListener {
            // Reemplazar el fragmento ImagenesFragment por el fragmento AlbumFragment
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, AlbumFragment())
            transaction.addToBackStack(null)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.commit()
        }

        imageView.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        // Manejar el evento de hacer clic en el botón "GUARDAR" para guardar la imagen y descripción
        buttonGuardar.setOnClickListener {
                val albumName = chipAlbum.text.toString() // Obtén el nombre del álbum seleccionado
                val description = editText.text.toString() // Obtén la descripción
                val selectedUri = selectedImageUri

                if (selectedUri != null && selectedAlbumName.isNotEmpty()) {
                    // Si se seleccionó una imagen, guárdala en la base de datos
                    val imageStream = requireContext().contentResolver.openInputStream(selectedUri)
                    if (imageStream != null) {
                        val imageBytes = imageStream.readBytes()
                        val imagen = DatabaseHelper.Imagen(0, albumName, imageBytes, description)
                        dbHelper.insertImagen(imagen)
                        imageStream.close() // Cierra el flujo después de usarlo
                    } else {
                        // Manejar el caso en que no se pudo abrir el flujo de entrada
                        // Esto podría ser una imagen no válida
                    }
                } else {
                    // Manejar el caso en que no se seleccionó una imagen
                }

            // Lógica adicional si es necesario
        }


        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ImagenesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImagenesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}