package com.example.cinetix

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinetix.Adapter.DateAdapter
import com.example.cinetix.Adapter.SeatListAdapter
import com.example.cinetix.Adapter.TimeAdapter
import com.example.cinetix.Models.Film
import com.example.cinetix.Models.Seat
import com.example.cinetix.databinding.ActivitySeatListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SeatListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeatListBinding
    private lateinit var film: Film
    private val selectedSeats = mutableSetOf<Int>()
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        getIntentExtras()
        setupSeatList()
        setupDateAndTimePickers()

        // Full-screen mode
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding.downloadTicketButton.setOnClickListener {
            generatePdf()
        }


        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupSeatList() {
        binding.seatRecyclerView.layoutManager = GridLayoutManager(this, 7)

        val seatList = generateSeatList(81)
        val seatAdapter = SeatListAdapter(seatList, this, selectedSeats, object : SeatListAdapter.SelectedSeat {
            override fun Return(selectedSeatIndex: Int, isSelected: Boolean) {
                if (isSelected) {
                    selectedSeats.add(selectedSeatIndex)
                } else {
                    selectedSeats.remove(selectedSeatIndex)
                }
                updateSelectedSeatInfo()
            }
        })

        binding.seatRecyclerView.adapter = seatAdapter
    }

    private fun setupDateAndTimePickers() {
        binding.TimeRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.TimeRecyclerView.adapter = TimeAdapter(generateTimeSlots())

        binding.dateRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.dateRecyclerView.adapter = DateAdapter(generateDateList())
    }

    private fun getIntentExtras() {
        film = intent.getParcelableExtra("film") ?: return
    }

    private fun generateSeatList(totalSeats: Int): MutableList<Seat> {
        val unavailableSeats = setOf(2, 20, 33, 41, 50, 72, 73)
        return MutableList(totalSeats) { index ->
            val status = if (index in unavailableSeats) Seat.SeatStatus.UNAVAILABLE else Seat.SeatStatus.AVAILABLE
            Seat(status, "")
        }
    }

    private fun generateTimeSlots(): List<String> {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return (0 until 24 step 2).map { LocalTime.of(it, 0).format(formatter) }
    }

    private fun generateDateList(): List<String> {
        val formatter = DateTimeFormatter.ofPattern("EEE/dd/MMM")
        return (0 until 7).map { LocalDate.now().plusDays(it.toLong()).format(formatter) }
    }

    @SuppressLint("SetTextI18n")
    private fun updateSelectedSeatInfo() {
        val selectedCount = selectedSeats.size
        val totalPrice = selectedCount * 50.0

        val formatter = DecimalFormat("#.##")
        binding.numberSelectedText.text = "$selectedCount Seat(s) Selected"
        binding.priceTxt.text = "$${formatter.format(totalPrice)}"
    }

    @SuppressLint("DefaultLocale")
    private fun generatePdf() {
        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name") ?: user.email ?: "Unknown"
                        val seatNumbers = selectedSeats.joinToString(", ") { "Seat $it" }
                        val selectedDate = binding.dateRecyclerView.adapter?.let { adapter ->
                            (adapter as DateAdapter).getSelectedDate()
                        } ?: "Unknown Date"
                        val selectedTime = binding.TimeRecyclerView.adapter?.let { adapter ->
                            (adapter as TimeAdapter).getSelectedTime()
                        } ?: "Unknown Time"
                        val totalPrice = selectedSeats.size * 50.0
                        val formattedTotalPrice = String.format("%.2f", totalPrice)
                        val pdfDocument = PdfDocument()
                        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                        val page = pdfDocument.startPage(pageInfo)

                        val canvas = page.canvas
                        val paint = Paint()
                        paint.textSize = 16f
                        canvas.drawText("Ticket", 80f, 50f, paint)
                        canvas.drawText("Name: $userName", 80f, 80f, paint)
                        canvas.drawText("Seats: $seatNumbers", 80f, 110f, paint)
                        canvas.drawText("Date: $selectedDate", 80f, 140f, paint)
                        canvas.drawText("Time: $selectedTime", 80f, 170f, paint)
                        canvas.drawText("Note: Please Pay directly with amount $$formattedTotalPrice", 80f, 200f, paint)

                        pdfDocument.finishPage(page)

                        val filePath = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ticket.pdf")
                        try {
                            pdfDocument.writeTo(FileOutputStream(filePath))
                            pdfDocument.close()
                            openPdf(filePath)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun openPdf(file: File) {
        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }
}