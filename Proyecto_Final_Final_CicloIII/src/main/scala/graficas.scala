import com.github.tototoshi.csv.*

/* Nuevos imports */
import org.nspl.*
import org.nspl.awtrenderer.*
import org.nspl.data.HistogramData

import org.nspl.saddle.*
import org.saddle.*
import org.saddle.order.*
import org.saddle.csv.*
import org.saddle.scalar.ScalarTagDouble
import org.saddle.index.InnerJoin
import org.nspl.Renderer
import org.nspl.RelFontSize
import org.nspl.Colormap
import java.io.File

object graficas {
  @main
  def somebody(): Unit = {
    val ruta = "C:/Users/D E L L/Documents/Nahomi/CICLO III/PFR/ArchivoPIntegrador/dsAlineacionesXTorneo.csv"
    val reader = CSVReader.open(new File(ruta))
    val contentFile2: List[Map[String, String]] =
      reader.allWithHeaders()

    val ruta2 = "C:/Users/D E L L/Documents/Nahomi/CICLO III/PFR/ArchivoPIntegrador/dsPartidosYGoles.csv"
    val reader2 = CSVReader.open(new File(ruta2))
    val contentFile: List[Map[String, String]] =
      reader2.allWithHeaders()

    def datosRaros(data: List[Map[String, String]]) = {
      val datosNA = data.flatMap(_.values).count(_.equals("NA"))
      val datosNAa = datosNA + data.flatMap(_.values).count(_.equals(""))
      val datosNotAva = datosNAa + data.flatMap(_.values).count(_.equals("not available"))
      val datosNotAppl = datosNotAva + data.flatMap(_.values).count(_.equals("not applicable"))
      val datosVacios = datosNotAppl + data.flatMap(_.values).count(_.equals("\\s"))

      datosVacios
    }

    println(s"Celdas vacías o con valores N/A PARTIDOS Y GOLES: ${datosRaros(contentFile)}")
    println(s"Celdas vacías o con valores N/A TORNEOS Y ALINEACIONES: ${datosRaros(contentFile2)}")

    def graficas(data: List[Map[String, String]], nombre: String): Unit = {
      val datosNA = data.flatMap(_.values).count(_.equals("NA")).toDouble
      val datosNAa = data.flatMap(_.values).count(_.equals("")).toDouble
      val datosNotAva = data.flatMap(_.values).count(_.equals("not available")).toDouble
      val datosNotAppl = data.flatMap(_.values).count(_.equals("not applicable")).toDouble
      val datosVacios = data.flatMap(_.values).count(_.equals("\\s")).toDouble

      val listaDatosMalos = Series("NA" -> datosNA, "x" -> datosNAa, "NAv" -> datosNotAva, "NAp" -> datosNotAppl, "/s" -> datosVacios)

      val grafico = barplotHorizontal(listaDatosMalos,xLabFontSize = Some(RelFontSize(0.8)))(par.withMain(s"${nombre}"))

      pngToFile(new File(s"C:/Users/D E L L/Documents/Nahomi/CICLO III/PROYECTO FINAL FINAL FINAL/${nombre}.png"), grafico.build, 2000)
    }

    def graficoScatter(data: List[Map[String, String]], nombre: String) = {
      def valoresDoBuedos(valor: String) =
        if (valor == "not available" || valor == "not applicable" || valor == "NA" || valor == "\\s") {
          0
        } else {
          valor.toDouble
        }

      val sacarValores = data
        .map(x => Map(x("matches_match_id") -> (valoresDoBuedos(x("matches_home_team_score")) + valoresDoBuedos(x("matches_away_team_score")))))
        .distinct

      val listaPartidos = Series("1" -> 5.0, "2" -> 3.0, "3" -> 3.0, "4" -> 4.0, "5" -> 1.0, "6" -> 3.0,
        "7" -> 4.0, "8" -> 3.0, "9" -> 1.0, "10" -> 1.0, "11" -> 9.0, "12" -> 4.0,
        "13" -> 1.0, "14" -> 4.0, "15" -> 4.0, "16" -> 7.0, "17" -> 7.0, "18" -> 6.0)

      val logaxis =
        barplotHorizontal(listaPartidos, xLabFontSize = Some(RelFontSize(0.8))
        )(par.withMain(s"${nombre}"))

      pngToFile(new File(s"C:/Users/D E L L/Documents/Nahomi/CICLO III/PROYECTO FINAL FINAL FINAL/${nombre}.png"),logaxis.build, 2000)

      sacarValores.foreach(println)
    }

    // graficas(contentFile, "PARTIDOS Y GOLES")
    // graficas(contentFile2, "TORNEOS Y ALINEACIONES")

    graficoScatter(contentFile, "GOLES DEL MUNDIAL DE 1930")
  }
}

/*
* 1930.1 -> 5.0,
1930.2 -> 3.0,
1930.3 -> 3.0,
1930.4 -> 4.0,
1930.5 -> 1.0,
1930.6 -> 3.0,
1930.7 -> 4.0,
1930.8 -> 3.0,
1930.9 -> 1.0,
1930.10 -> 1.0,
1930.11 -> 9.0,
1930.12 -> 4.0,
1930.13 -> 1.0,
1930.14 -> 4.0,
1930.15 -> 4.0,
1930.16 -> 7.0,
1930.17 -> 7.0,
1930.18 -> 6.0
*
*
* */
