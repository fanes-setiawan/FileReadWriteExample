package com.example.filereadwriteexample

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.filereadwriteexample.ui.theme.FileReadWriteExampleTheme
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FileReadWriteExampleTheme {
                var fileName by remember { mutableStateOf(TextFieldValue("")) }
                var data by remember { mutableStateOf(TextFieldValue("")) }
                var files by remember { mutableStateOf(listOf<String>()) }
                var selectedFileContent by remember { mutableStateOf<String?>(null) }

                fun updateFilesList() {
                    files = filesDir.list()?.toList() ?: listOf()
                }

                LaunchedEffect(Unit) {
                    updateFilesList()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .padding(16.dp)
                        ) {
                            TextField(
                                value = fileName,
                                onValueChange = { fileName = it },
                                label = { Text("File Name") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )
                            TextField(
                                value = data,
                                onValueChange = { data = it },
                                label = { Text("Enter Data") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )
                            Button(
                                onClick = {
                                    if (fileName.text.isNotEmpty() && data.text.isNotEmpty()) {
                                        writeToFile(fileName.text, data.text)
                                        data = TextFieldValue("")
                                        fileName = TextFieldValue("")
                                        updateFilesList()
                                    } else {
                                        Toast.makeText(this@MainActivity, "Please enter both file name and data.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Text("Save to File")
                            }
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(files.size) { index ->
                                    Text(
                                        text = files[index],
                                        fontSize = 18.sp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .clickable {
                                                selectedFileContent = readFromFile(files[index])
                                            }
                                    )
                                }
                            }
                        }
                    }
                )

                selectedFileContent?.let {
                    Dialog(onDismissRequest = { selectedFileContent = null }) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = it, fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
                                Button(onClick = { selectedFileContent = null }) {
                                    Text("Close")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun writeToFile(fileName: String, data: String) {
        try {
            val fileOutputStream: FileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(data.toByteArray())
            fileOutputStream.close()
            Toast.makeText(this, "File saved successfully.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "File write failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readFromFile(fileName: String): String {
        return try {
            val fileInputStream: FileInputStream = openFileInput(fileName)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
            fileInputStream.close()
            stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            "File read failed: ${e.message}"
        }
    }
}
