import java.net.URL
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

fun main(args: Array<String>) {
    val radarUrl = "https://radar.weather.gov/ridge/standard/KHTX_loop.gif"

    val imageIcon = ImageIcon(URL(radarUrl))
    val label = JLabel(imageIcon)

    val frame = JFrame("NOAA Radar for North Alabama")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.contentPane.add(label)
    frame.pack()
    frame.isVisible = true
}