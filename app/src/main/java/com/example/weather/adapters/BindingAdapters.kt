package com.example.weather.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.weather.R
import com.example.weather.domain.WeatherApiStatus
import com.google.android.material.appbar.CollapsingToolbarLayout
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * This binding adapter displays a numeric value as a degree.
 */
@BindingAdapter("temp")
fun CollapsingToolbarLayout.toDegrees(value: Double) {
    val intValue = value.roundToInt()
    "$intValue\u00B0".also { title = it }
}

/**
 * This binding adapter displays a numeric value as a degree.
 */
@BindingAdapter("temperature")
fun TextView.toDegrees(value: Double) {
    val intValue = value.roundToInt()
    "$intValue\u00B0".also { text = it }
}

/**
 * This binding adapter determines the day of the week given a date-string. Possible solution below
 * for android version greater of equal to Oreo.
 *
 *  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
 *      val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
 *      val localDate = LocalDate.parse(dateString, dateFormatter)
 *      val dayOfWeek = localDate.dayOfWeek
 *      text = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US)
 *  }
 */
@BindingAdapter("dayOfWeek")
fun TextView.setDayOfWeek(dateString: String?) {
    val mDate: String = dateString ?: "2021-01-01 00:00:00"
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val newDate = simpleDateFormat.parse(mDate)
    val formatter: DateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    text = formatter.format(newDate!!)
}

/**
 * This binding adapter sets the image depending on the condition id.
 */
@BindingAdapter("src")
fun ImageView.setCondition(condition: Int) {
    setImageResource(
        when (condition) {
            800 -> R.drawable.ic_clear
            in 801..804 -> R.drawable.ic_cloudy
            else -> R.drawable.ic_rain
        }
    )
}

/**
 * This binding adapter modifies the visibility of the layout.
 */
@BindingAdapter("visibility")
fun View.setVisibility(status: WeatherApiStatus?) {
    when (status) {
        WeatherApiStatus.LOADING -> {
            visibility = View.INVISIBLE
        }
        WeatherApiStatus.ERROR -> {
            visibility = View.INVISIBLE
        }
        WeatherApiStatus.DONE -> {
            visibility = View.VISIBLE
        }
    }
}
