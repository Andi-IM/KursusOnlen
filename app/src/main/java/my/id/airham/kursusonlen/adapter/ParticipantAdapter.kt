package my.id.airham.kursusonlen.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.id.airham.kursusonlen.R
import my.id.airham.kursusonlen.entity.Participant

// kelas adapter untuk menghandle recyclerview
class ParticipantAdapter(private val onItemClickCallBack: OnItemClickCallback) :
    RecyclerView.Adapter<ParticipantAdapter.ViewHolder>() {

    var list = ArrayList<Participant>()
        set(list) {
            if (list.size > 0) {
                this.list.clear()
            }
            this.list.addAll(list)
        }

    fun addItem(participant: Participant) {
        this.list.add(participant)
        notifyItemInserted(this.list.size - 1)
    }

    fun updateItem(position: Int, participant: Participant) {
        this.list[position] = participant
        notifyItemChanged(position, participant)
    }

    fun removeItem(position: Int) {
        this.list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, this.list.size)
    }

    // ketika item view dibangun
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    // ketika setiap komponen di dalam view didaftarkan (binding)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rowHolder: ViewHolder = holder
        val participant = rowHolder.adapterPosition

        if (participant == 0) {
            // bagian header
            rowHolder.no.setBackgroundResource(R.drawable.table_header_cell_bg)
            rowHolder.name.setBackgroundResource(R.drawable.table_header_cell_bg)
            rowHolder.address.setBackgroundResource(R.drawable.table_header_cell_bg)
            rowHolder.phoneNumber.setBackgroundResource(R.drawable.table_header_cell_bg)
            rowHolder.maps.setBackgroundResource(R.drawable.table_header_cell_bg)
            rowHolder.photo_title.setBackgroundResource(R.drawable.table_header_cell_bg)

            rowHolder.no.text = "No. "
            rowHolder.name.text = "Nama"
            rowHolder.address.text = "Alamat"
            rowHolder.phoneNumber.text = "No. Telp"
            rowHolder.maps.text = "Tag Peta"
            rowHolder.photo_title.visibility = View.VISIBLE
            rowHolder.photo_title.text = "Foto"
            // image view dihilangkan
            rowHolder.photo.visibility = View.GONE

        } else {
            val model = list[participant - 1] // karena udah ada judul sebagai item pertama

            rowHolder.no.setBackgroundResource(R.drawable.table_content_cell_bg)
            rowHolder.name.setBackgroundResource(R.drawable.table_content_cell_bg)
            rowHolder.address.setBackgroundResource(R.drawable.table_content_cell_bg)
            rowHolder.phoneNumber.setBackgroundResource(R.drawable.table_content_cell_bg)
            rowHolder.maps.setBackgroundResource(R.drawable.table_content_cell_bg)

            rowHolder.no.text = position.toString()
            rowHolder.name.text = model.name
            rowHolder.address.text = model.address
            rowHolder.phoneNumber.text = model.phoneNumber
            rowHolder.maps.text = "${model.latitude}, ${model.longitude}"
            holder.photo.setImageBitmap(BitmapFactory.decodeFile(model.photoUri))
            rowHolder.item.setOnClickListener {
                onItemClickCallBack.onItemClicked(model, position)
            }
        }
    }

    override fun getItemCount(): Int = list.size + 1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var no: TextView = view.findViewById(R.id.txt_item_no)
        var name: TextView = view.findViewById(R.id.txt_item_nama)
        var address: TextView = view.findViewById(R.id.txt_item_alamat)
        var phoneNumber: TextView = view.findViewById(R.id.txt_item_phone)
        var maps: TextView = view.findViewById(R.id.txt_item_alamat_maps)
        var photo_title: TextView = view.findViewById(R.id.txt_item_image)
        var photo: ImageView = view.findViewById(R.id.img_item_foto)
        var item = view.findViewById<LinearLayout>(R.id.item_layout)
    }

    // menghandle ketika suatu item diklik
    interface OnItemClickCallback {
        fun onItemClicked(selectedParticipant: Participant?, position: Int?)
    }
}