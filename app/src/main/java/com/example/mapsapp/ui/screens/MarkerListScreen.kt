package com.example.mapsapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.viewmodels.MyViewModel
import com.google.android.gms.maps.model.Marker

@Composable
fun MarkerListScreen() {
    val myViewModel = viewModel<MyViewModel>()
    val studentName: String by myViewModel.markerName.observeAsState("")
    val studentMark: String by myViewModel.markerMark.observeAsState("")
    val studentsList by myViewModel.markersList.observeAsState(emptyList<Marker>())
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().weight(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text("Create new student", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            TextField(value = studentName, onValueChange = { myViewModel.editStudentName(it) })
            TextField(value = studentMark, onValueChange = { myViewModel.editStudentMark(it) })
            Button(onClick = { myViewModel.insertNewMarker(studentName, studentMark) }) {
                Text("Insert")
            }
        }
        myViewModel.getAllMarkers()
        LazyColumn(Modifier.fillMaxWidth().weight(0.6f)) {
            items(studentsList) { student ->
                val dissmissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            myViewModel.deleteStudent(student.id.toString())
                            true
                        } else { false }
                    }
                )
                SwipeToDismissBox(state = dissmissState, backgroundContent = {
                    Box(Modifier.fillMaxSize().background(Color.Red),contentAlignment = Alignment.BottomEnd) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    }
                }) {
                    StudentItem(student) { navigateToDetail(student.id.toString()) }
                }
            }
        }


    }

}


@Composable
fun StudentItem(student: Student, navigateToDetail: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth().background(Color.LightGray).border(width = 2.dp, Color.DarkGray)
            .clickable { navigateToDetail(student.id.toString()) }) {
        Row(Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(student.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(text = "Mark: ${student.mark}")
        }
    }
}
