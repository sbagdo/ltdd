package com.example.crudfirebasestore

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

class CourseDetailsActivity : ComponentActivity() {
    private val courseList = mutableStateListOf<Course>()
    private var isLoading by mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { androidx.compose.material3.Text("Danh sách khóa học", color = Color.White) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        )
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { loadCourses() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.Text("Refresh")
                        }
                        Spacer(Modifier.height(12.dp))

                        when {
                            isLoading -> CircularProgressIndicator()
                            courseList.isEmpty() -> androidx.compose.material3.Text("Không có dữ liệu khóa học.")
                            else -> LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(courseList, key = { it.courseID }) { course ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val intent = Intent(
                                                    this@CourseDetailsActivity,
                                                    UpdateCourse::class.java
                                                )
                                                intent.putExtra("courseID", course.courseID)
                                                intent.putExtra("courseName", course.courseName)
                                                intent.putExtra("courseDuration", course.courseDuration)
                                                intent.putExtra("courseDescription", course.courseDescription)
                                                startActivity(intent)
                                            }
                                    ) {
                                        Column(Modifier.padding(16.dp)) {
                                            androidx.compose.material3.Text(
                                                text = course.courseName,
                                                color = Color(0xFF4CAF50),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(Modifier.height(6.dp))
                                            androidx.compose.material3.Text("Thời lượng: ${course.courseDuration}")
                                            Spacer(Modifier.height(4.dp))
                                            androidx.compose.material3.Text("Mô tả: ${course.courseDescription}")
                                            Spacer(Modifier.height(4.dp))
                                            androidx.compose.material3.Text(
                                                text = "ID: ${course.courseID}",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                            androidx.compose.material3.Text(
                                                text = "Nhấn để cập nhật",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        loadCourses()
    }

    private fun loadCourses() {
        isLoading = true
        FirebaseFirestore.getInstance()
            .collection("Courses")
            .get()
            .addOnSuccessListener { snapshot ->
                courseList.clear()
                snapshot.documents.forEach { document ->
                    val course = document.toObject(Course::class.java)
                    if (course != null) {
                        course.courseID = document.id
                        courseList.add(course)
                    }
                }
                isLoading = false
                if (courseList.isEmpty()) {
                    Toast.makeText(this, "Không tìm thấy khóa học nào.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { error ->
                isLoading = false
                Toast.makeText(this, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_LONG).show()
            }
    }
}
