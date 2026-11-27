package com.example.crudfirebasestore

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var courseName by remember { mutableStateOf("") }
                    var courseDuration by remember { mutableStateOf("") }
                    var courseDescription by remember { mutableStateOf("") }

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("CrudFirebaseStore", color = Color.White) },
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Thêm khóa học", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = courseName,
                                onValueChange = { courseName = it },
                                label = { Text("Tên khóa học") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(Modifier.height(8.dp))

                            OutlinedTextField(
                                value = courseDuration,
                                onValueChange = { courseDuration = it },
                                label = { Text("Thời lượng") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(Modifier.height(8.dp))

                            OutlinedTextField(
                                value = courseDescription,
                                onValueChange = { courseDescription = it },
                                label = { Text("Mô tả khóa học") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    when {
                                        courseName.isBlank() -> Toast.makeText(
                                            this@MainActivity, "Vui lòng nhập tên khóa học",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        courseDuration.isBlank() -> Toast.makeText(
                                            this@MainActivity, "Vui lòng nhập thời lượng",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        courseDescription.isBlank() -> Toast.makeText(
                                            this@MainActivity, "Vui lòng nhập mô tả",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        else -> {
                                            val id = UUID.randomUUID().toString()
                                            val course = Course(
                                                id,
                                                courseName.trim(),
                                                courseDuration.trim(),
                                                courseDescription.trim()
                                            )
                                            FirebaseFirestore.getInstance()
                                                .collection("Courses")
                                                .document(id)
                                                .set(course)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "Đã thêm khóa học",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    courseName = ""
                                                    courseDuration = ""
                                                    courseDescription = ""
                                                }
                                                .addOnFailureListener { error ->
                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "Không thể thêm: ${error.message}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add Data")
                            }

                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    startActivity(
                                        android.content.Intent(
                                            this@MainActivity,
                                            CourseDetailsActivity::class.java
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View Courses")
                            }
                        }
                    }
                }
            }
        }
    }
}
