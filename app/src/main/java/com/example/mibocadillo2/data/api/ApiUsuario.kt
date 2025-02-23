import com.example.mibocadillo2.data.model.Usuario
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiUsuario {
    @GET("Usuario/{uid}.json")
    suspend fun getUsuarioByUid(@Path("uid") uid: String): Usuario?
}
