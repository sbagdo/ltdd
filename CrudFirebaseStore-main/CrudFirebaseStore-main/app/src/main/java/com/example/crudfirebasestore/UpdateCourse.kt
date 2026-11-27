package com.example.crudfirebasestore

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

class UpdateCourse : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val courseName = intent.getStringExtra("courseName").orEmpty()
        val courseDuration = intent.getStringExtra("courseDuration").orEmpty()
        val courseDescription = intent.getStringExtra("courseDescription").orEmpty()
        val courseID = intent.getStringExtra("courseID").orEmpty()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Cập nhật khóa học",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color(0xFF4CAF50)
                                )
                            )
                        }
                    ) { padding ->
                        UpdateCourseUI(
                            context = this@UpdateCourse,
                            courseName = courseName,
                            courseDuration = courseDuration,
                            courseDescription = courseDescription,
                            courseID = courseID,
                            modifier = Modifier.padding(padding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateCourseUI(
    context: Context,
    courseName: String,
    courseDuration: String,
    courseDescription: String,
    courseID: String,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(courseName) }
    var duration by remember { mutableStateOf(courseDuration) }
    var description by remember { mutableStateOf(courseDescription) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên khóa học") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Thời lượng khóa học") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Mô tả khóa học") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                when {
                    name.isBlank() -> Toast.makeText(
                        context,
                        "Vui lòng nhập tên khóa học",
                        Toast.LENGTH_SHORT
                    ).show()

                    duration.isBlank() -> Toast.makeText(
                        context,
                        "Vui lòng nhập thời lượng khóa học",
                        Toast.LENGTH_SHORT
                    ).show()

                    description.isBlank() -> Toast.makeText(
                        context,
                        "Vui lòng nhập mô tả khóa học",
                        Toast.LENGTH_SHORT
                    ).show()

                    courseID.isBlank() -> Toast.makeText(
                        context,
                        "Không tìm thấy ID khóa học",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> updateDataToFirebase(
                        courseID,
                        name.trim(),
                        duration.trim(),
                        description.trim(),
                        context
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Data", modifier = Modifier.padding(8.dp))
        }
    }
}

private fun updateDataToFirebase(
    courseID: String,
    name: String,
    duration: String,
    description: String,
    context: Context
) {
    val updatedCourse = Course(
        courseID = courseID,
        courseName = name,
        courseDuration = duration,
        courseDescription = description
    )

    FirebaseFirestore.getInstance()
        .collection("Courses")
        .document(courseID)
        .set(updatedCourse)
        .addOnSuccessListener {
            Toast.makeText(
                context,
                "Cập nhật khóa học thành công",
                Toast.LENGTH_SHORT
            ).show()

            (context as? Activity)?.finish()
        }
        .addOnFailureListener { error ->
            Toast.makeText(
                context,
                "Không thể cập nhật: ${error.message}",
                Toast.LENGTH_LONG
            ).show()
        }
}