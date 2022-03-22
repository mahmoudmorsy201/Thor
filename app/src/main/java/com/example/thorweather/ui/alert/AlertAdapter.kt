package com.example.thorweather.ui.alert

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thorweather.data.model.AlertModel
import com.example.thorweather.databinding.ItemAlertBinding
import com.example.thorweather.util.SharedPreferencesFactory
import com.example.thorweather.util.WeatherUtil
import com.example.thorweather.util.notifier.AlertNotificationReceiver
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


class AlertAdapter(
    var alertsList: ArrayList<AlertModel>,
    _alertViewModel: AlertViewModel
) :
    RecyclerView.Adapter<AlertAdapter.NotificationViewHolder>() {
    private var removedPosition = 0
    lateinit var removedObject: AlertModel
    private var alertViewModel = _alertViewModel
    lateinit var sharedPreferencesFactory: SharedPreferencesFactory

    class NotificationViewHolder(var view: ItemAlertBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemAlertBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        sharedPreferencesFactory = SharedPreferencesFactory(context = parent.context)
        return NotificationViewHolder(binding)
    }

    fun changeData(newList: List<AlertModel>) {
        alertsList.clear()
        alertsList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = alertsList.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = alertsList[position]
        holder.view.dateFromTextView.text = WeatherUtil.converterToDay(
            item.startDate,
            sharedPreferencesFactory.getLanguage().toString()
        )
        holder.view.dateToTextView.text = WeatherUtil.converterToDay(
            item.endDate,
            sharedPreferencesFactory.getLanguage().toString()
        )
        holder.view.fromHourTextView.text =
            WeatherUtil.converterHourAndMinutes(
                item.fireAlertTime,
                sharedPreferencesFactory.getLanguage().toString()
            )
        holder.view.toHourTextView.text = WeatherUtil.converterHourAndMinutes(
            item.fireAlertTime,
            sharedPreferencesFactory.getLanguage().toString()
        )
    }


    fun removeSelectedAlertFromAdapter(viewHolder: RecyclerView.ViewHolder) {
        removedPosition = viewHolder.adapterPosition
        removedObject = alertsList[viewHolder.adapterPosition]
        alertsList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
        Snackbar.make(
            viewHolder.itemView,
            "Removed",
            Snackbar.LENGTH_LONG
        ).apply {
            setAction("Undo") {
                alertsList.add(removedPosition, removedObject)
                notifyItemInserted(removedPosition)
            }
            addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                }

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        alertViewModel.deleteAlert(removedObject)
                        removeAlertFromSystem(removedObject.id, context)
                    }
                }
            })
        }.show()
    }

    private fun removeAlertFromSystem(id: Long, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}

