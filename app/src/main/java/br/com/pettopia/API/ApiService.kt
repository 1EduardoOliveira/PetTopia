package br.com.pettopia.API

import androidx.annotation.Nullable
import br.com.pettopia.model.AnimalModel
import br.com.pettopia.model.AuthResponse
import br.com.pettopia.model.ClienteModel
import br.com.pettopia.model.DenunciaModel
import br.com.pettopia.model.Documentos
import br.com.pettopia.model.LoginRequest
import br.com.pettopia.model.ServicosModel
import br.com.pettopia.model.ValidarToken
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @GET("api/cliente/ListarDocumentosCliente/{idCliente}")
    suspend fun listarDocumentosCliente(@Path("idCliente") idCliente: Long): List<Documentos>

    @POST("api/cliente/cadastrar")
    suspend fun  cadastrarCliente(@Body clienteModel: ClienteModel): Response<AuthResponse>

    @POST("api/LoginWeb/loginWeb")
    suspend fun  loginCliente(@Body loginRequest: LoginRequest): Response<AuthResponse>

    @POST("api/cliente/RealizarDenuncia")
    suspend fun  realizarDenuncia(@Body denunciaModel: DenunciaModel): Response<DenunciaModel>

    @DELETE("api/cliente/ExcluirDenuncia/{idDenuncia}")
    suspend fun  deleteDenuncia(@Path("idDenuncia") idDenuncia : Long): Response<Void>

    @PUT("api/cliente/AtualizarDenuncia/{idDenuncia}")
    suspend fun editarDenuncia(@Path("idDenuncia") idDenuncia: Long , @Body denunciaModel: DenunciaModel): Response<DenunciaModel>

    @PUT("api/cliente/editarDados/{idCliente}")
    suspend fun  editarDados(@Body clienteModel: ClienteModel, @Path("idCliente") idCliente : Long): Response<ClienteModel>

    @DELETE("api/cliente/deletarConta/{idCliente}")
    suspend fun  deletarConta(@Path("idCliente") idCliente : Long): Response<Void>

    @PUT("api/cliente/editarAnimal/{idAnimal}")
    suspend fun  editarAnimal(@Path ("idAnimal") idAnimal: Long): Response<AnimalModel>

    @DELETE("api/cliente/deleteAnimal/{idAnimal}")
    suspend fun  deleteAnimal(@Path("idAnimal") idAnimal : Long): Response<Void>

//    @POST("api/cliente/cadastrar/animal{idAnimal}")
//    suspend fun cadastrarAnimal(@Path("idAnimal")idAnimal: Long): Response<AuthResponse>

    @GET("/api/cliente/BuscarDenuncias/{idCliente}")
    suspend fun listarDenuncia(@Path("idCliente") idCliente: Long): Response<List<DenunciaModel>>

    @Multipart
    @POST("api/cliente/enviarDocumentosTeste")
    suspend fun enviarDocumentos(
        @Part("idCliente") idCliente: RequestBody,
        @Part("idAnimal") idAnimal: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Documentos>


    @GET("api/cliente/ExibirAdocoesAnimais")
    suspend fun getAnimaisParaAdocao(): Response<List<AnimalModel>>


    @Multipart
    @POST("api/cliente/cadastrar/animal")
    suspend fun cadastrarAnimal(
        @Part("animal") animal: RequestBody,
        @Part fotoAnimal: MultipartBody.Part
    ): Response<AnimalModel>

    @POST("api/cliente/esqueci-senha")
    suspend fun esqueciasenha(@Body clienteModel: ClienteModel):Response<ClienteModel>

    @POST("api/cliente/validar-token")
    suspend fun validartoken(@Body validarToken: ValidarToken):Response<ValidarToken>

    @PATCH("api/cliente/atualizar-senha")
    suspend fun atualizarsenha(@Body clienteModel: ClienteModel):Response<ClienteModel>

    @GET("api/cliente/doacoes/{idCliente}")
    suspend fun buscarComprovantes(@Path("idCliente") idCliente: Long): List<ServicosModel>

    @GET("api/cliente/comprovante/{id}")
    suspend fun baixarComprovante(@Body servicosModel: ServicosModel):Call<ServicosModel>

}