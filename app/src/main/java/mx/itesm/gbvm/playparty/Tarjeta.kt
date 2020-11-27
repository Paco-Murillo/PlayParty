package mx.itesm.gbvm.playparty

data class Tarjeta(var points: Int = 0, var cancion: String = "", var artista: String = "", var idImagen: String = "", var tiempo: Int = 0, var idSong: String = "")
{
    class Comparator: kotlin.Comparator<Tarjeta>{
        override fun compare(p0: Tarjeta?, p1: Tarjeta?): Int {
            if (p0 == null || p1 == null){
                return 0
            }
            return p0.points.compareTo(p1.points)
        }
    }
}