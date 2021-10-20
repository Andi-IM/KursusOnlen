package my.id.airham.kursusonlen.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import my.id.airham.kursusonlen.BuildConfig
import my.id.airham.kursusonlen.R
import my.id.airham.kursusonlen.data.Participant
import my.id.airham.kursusonlen.data.UserLocation
import my.id.airham.kursusonlen.db.DatabaseContract
import my.id.airham.kursusonlen.db.ParticipantHelper
import java.io.File
import java.util.*

class RegisterUpdateActivity : AppCompatActivity(), View.OnClickListener, LocationListener {

    companion object {
        const val APP_TAG = "KursusOnlen"
        const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
        const val photoFileName = "photo.jpg"
        const val EXTRA_PARTICIPANT = "extra_participant"
        const val EXTRA_POSITION = "extra_position"
        private const val locationPermissionCode = 111

        const val RESULT_ADD = 101
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

    private var isEdit = false
    private var photoFile: File? = null
    private lateinit var locationManager: LocationManager
    private var location: UserLocation? = null

    private lateinit var editNama: EditText
    private lateinit var editAlamat: EditText
    private lateinit var editNomorPonsel: EditText
    private lateinit var groupJenisKelamin: RadioGroup
    private lateinit var radioJK: RadioButton
    private lateinit var btnLocation: Button
    private lateinit var lbLokasi: TextView
    private lateinit var btnTambahFoto: Button
    private lateinit var lbPhoto: TextView
    private lateinit var btnSubmit: Button

    private lateinit var participantHelper: ParticipantHelper
    private var participant: Participant? = null
    private var position: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_update)

        editNama = findViewById(R.id.edit_nama)
        editAlamat = findViewById(R.id.edit_alamat)
        editNomorPonsel = findViewById(R.id.edit_nomor_ponsel)
        groupJenisKelamin = findViewById(R.id.rg_jenis_kelamin)

        groupJenisKelamin.setOnCheckedChangeListener { group, checkedId ->
            radioJK = group.findViewById(checkedId)
        }

        btnLocation = findViewById(R.id.btn_cek_location)
        lbLokasi = findViewById(R.id.txt_lokasi_perkiraan)
        btnTambahFoto = findViewById(R.id.btn_add_photo)
        lbPhoto = findViewById(R.id.txt_nama_foto)
        btnSubmit = findViewById(R.id.submit_form)

        participantHelper = ParticipantHelper.getInstance(applicationContext)
        participantHelper.open()

        // jika ada data participant
        participant = intent.getParcelableExtra(EXTRA_PARTICIPANT)
        if (participant != null) {
            position = intent.getIntExtra(EXTRA_POSITION, 0)
            isEdit = true
        } else {
            participant = Participant()
        }

        val actionBarTitle: String
        val btnTitle: String

        if (isEdit) {
            actionBarTitle = "Update Data"
            btnTitle = "Update"

            participant?.let {
                editNama.setText(it.name)
                editAlamat.setText(it.address)
                editNomorPonsel.setText(it.phoneNumber)
                groupJenisKelamin.check(
                    if (it.gender?.lowercase(Locale.getDefault())?.trim() == "perempuan")
                        R.id.perempuan else R.id.laki_laki)
                lbLokasi.text = "${it.latitude}, ${it.longitude}"
                val fileName = it.photoUri?.trim()?.split("/")?.last()
                lbPhoto.text = fileName

            }
        } else {
            actionBarTitle = "Daftar"
            btnTitle = "Simpan"
        }

        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // dapatkan data dari intent lain.
        if (intent.getSerializableExtra("EXTRA_LOCATION") != null) {
            location = intent.getSerializableExtra("EXTRA_LOCATION") as? UserLocation
            lbLokasi.text = "${location?.latitude}, ${location?.longitude}"
        }

        btnLocation.setOnClickListener(this)
        btnTambahFoto.setOnClickListener(this)

        btnSubmit.text = btnTitle
        btnSubmit.setOnClickListener(this)
    }

    // menjalankan kamera
    private fun onLaunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFileUri(photoFileName)

        // file foto ada?
        if (photoFile != null) {
            val fileProvider: Uri = FileProvider.getUriForFile(
                Objects.requireNonNull(applicationContext),
                BuildConfig.APPLICATION_ID + ".provider",
                photoFile!!
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // apakah user diizinkan untuk akses kamera?
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }

        }
    }

    // mendapatkan foto dari uri (uri adalah link / lokasi tempat dimana foto berada)
    fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator + fileName)
    }

    // callback data dari kamera
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                // val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath) --> ini tidak digunakan untuk sementara
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview

                // pada tahap ini saya hanya memberi nama file dari foto
                lbPhoto.text = photoFile!!.name
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // action ketika tombol diklik
    override fun onClick(view: View) {
        // dapatkan id button
        when (view.id) {
            R.id.btn_cek_location -> onGetLocation()
            R.id.btn_add_photo -> onLaunchCamera()
            R.id.submit_form -> {
                val nama = editNama.text.toString().trim()
                val alamat = editAlamat.text.toString().trim()
                val noPonsel = editNomorPonsel.text.toString()
                val jenisKelamin = radioJK.text.toString().trim()

                val photoUri = photoFile?.absolutePath

                if (nama.isEmpty()){
                    editNama.error = "Tidak boleh kosong!"
                    return
                }

                // membungkus data ke dalam data class
                participant?.name = nama
                participant?.address = alamat
                participant?.phoneNumber = noPonsel
                participant?.gender = jenisKelamin
                participant?.latitude = location?.latitude!!
                participant?.longitude = location?.longitude!!
                participant?.photoUri = photoUri

                val intent = Intent(this@RegisterUpdateActivity, ListParticipantActivity::class.java)
                intent.putExtra(EXTRA_PARTICIPANT, participant)
                intent.putExtra(EXTRA_POSITION, position)

                // menampung data untuk disimpan ke dalam database
                val values = ContentValues()
                values.put(DatabaseContract.ParticipantColumns.NAME, nama)
                values.put(DatabaseContract.ParticipantColumns.ADDRESS, alamat)
                values.put(DatabaseContract.ParticipantColumns.PHONE_NUMBER, noPonsel)
                values.put(DatabaseContract.ParticipantColumns.GENDER, jenisKelamin)
                values.put(DatabaseContract.ParticipantColumns.LATITUDE, location?.latitude!!)
                values.put(DatabaseContract.ParticipantColumns.LONGITUDE, location?.longitude!!)
                values.put(DatabaseContract.ParticipantColumns.PHOTO_URI, photoUri)

                // jika edit maka resultnya UPDATE, jika bukan maka ADD
                if (isEdit) {
                    val result = participantHelper.update(participant?.id.toString(), values)
                    if (result > 0) {
                        setResult(RESULT_UPDATE, intent)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@RegisterUpdateActivity,
                            "Gagal Mengupdate Data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val result = participantHelper.insert(values)

                    if (result > 0) {
                        participant?.id = result.toInt()
                        setResult(RESULT_ADD, intent)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@RegisterUpdateActivity,
                            "Gagal Mendaftar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun onGetLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager // mendapatkan service untuk lokasi

        // meminta izin privasi kepada user untuk mengakses kamera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode
            )
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }
    }

    // mendapatkan hasil permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println("onRequestPermissionResult $requestCode")
        if (requestCode == locationPermissionCode) {
            // apakah diizinkan?
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onGetLocation()
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isEdit) {
            menuInflater.inflate(R.menu.menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    /*
        Konfirmasi dialog sebelum proses batal atau hapus
        close = 10
        deleteParticipant = 20
    */
    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose) {
            dialogTitle = "Batal"
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?"
        } else {
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?"
            dialogTitle = "Hapus Data"
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                if (isDialogClose) {
                    finish()
                } else {
                    val result = participantHelper.deleteById(participant?.id.toString()).toLong()
                    if (result > 0) {
                        val intent = Intent()
                        intent.putExtra(EXTRA_POSITION, position)
                        setResult(RESULT_DELETE, intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterUpdateActivity,
                            "Gagal menghapus data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    // Dapatkan data latitude/longitude
    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(locationData: Location) {
        location = UserLocation(locationData.latitude, locationData.longitude)
        lbLokasi.text =  "${location?.latitude}, ${location?.longitude}"
    }
}