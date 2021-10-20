package my.id.airham.kursusonlen.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import my.id.airham.kursusonlen.R
import my.id.airham.kursusonlen.adapter.ParticipantAdapter
import my.id.airham.kursusonlen.db.ParticipantHelper
import my.id.airham.kursusonlen.entity.Participant
import my.id.airham.kursusonlen.util.MappingHelper

class ListParticipantActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar : ProgressBar
    private lateinit var adapter: ParticipantAdapter

    val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        result ->
        if (result.data != null) {
            // Dipanggil ketika request code = ADD
            when(result.resultCode) {
                RegisterUpdateActivity.RESULT_ADD -> {
                    val participant = result?.data?.getParcelableExtra<Participant>(RegisterUpdateActivity.EXTRA_PARTICIPANT) as Participant
                    adapter.addItem(participant)
                    recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
                    showSnackbarMessage("Satu item ditambahkan")
                }
                RegisterUpdateActivity.RESULT_UPDATE -> {
                    val participant = result?.data?.getParcelableExtra<Participant>(RegisterUpdateActivity.EXTRA_PARTICIPANT) as Participant
                    val position = result.data?.getParcelableExtra<Participant>(RegisterUpdateActivity.EXTRA_POSITION) as Int
                    adapter.updateItem(position, participant)
                    recyclerView.smoothScrollToPosition(position)
                    showSnackbarMessage("Satu item berhasil diubah")
                }
                RegisterUpdateActivity.RESULT_DELETE -> {
                    val position = result?.data?.getParcelableExtra<Participant>(RegisterUpdateActivity.EXTRA_POSITION) as Int
                    adapter.removeItem(position)
                    showSnackbarMessage("Satu item telah dihapus")
                }
            }
        }
    }

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_participant)

        supportActionBar?.title = "Daftar Peserta"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressBar = findViewById(R.id.progressbar)
        recyclerView = findViewById(R.id.rc_participant_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val intent = Intent(this@ListParticipantActivity, RegisterUpdateActivity::class.java)
        adapter = ParticipantAdapter(object: ParticipantAdapter.OnItemClickCallback{
            override fun onItemClicked(selectedParticipant: Participant?, position: Int?) {
                intent.putExtra(RegisterUpdateActivity.EXTRA_PARTICIPANT, selectedParticipant)
                intent.putExtra(RegisterUpdateActivity.EXTRA_POSITION, position)
                resultLauncher.launch(intent)
            }
        })

        recyclerView.adapter = adapter

        if (savedInstanceState == null) {
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Participant>(EXTRA_STATE)
            print("list state ${list?.size}")

            if (list != null) {
                adapter.list = list
            }
        }
    }

    private fun loadNotesAsync() {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE
            val participantHelper = ParticipantHelper.getInstance(applicationContext)
            participantHelper.open()

            // mengambil data participant secara asynchronous
            val deferredParticipant = async(Dispatchers.IO){
                val cursor = participantHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }

            progressBar.visibility = View.INVISIBLE
            val participants = deferredParticipant.await()
            if (participants.size > 0 ){
                println("total participant ${participants.size}")
                println("participant name ${participants[0].name}")
                adapter.list = participants
            } else {
                adapter.list = ArrayList()
                showSnackbarMessage("Kosong")
            }
            participantHelper.close()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.list)
    }


    /**
     * Tampilkan snackbar
     *
     * @param message inputan message
     */
    private fun showSnackbarMessage(message: String) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show()
    }
}