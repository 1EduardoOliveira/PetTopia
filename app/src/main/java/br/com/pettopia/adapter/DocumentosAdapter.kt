package br.com.pettopia.adapter

import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.pettopia.R
import br.com.pettopia.model.Documentos


class DocumentosAdapter(
    private val documentosList: List<Documentos>
) : RecyclerView.Adapter<DocumentosAdapter.DocumentosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_documento, parent, false)
        return DocumentosViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentosViewHolder, position: Int) {
        val documento = documentosList[position]

        // Atualiza as informações na interface, mas não exibe o arquivo diretamente
        holder.textViewNomeDocumento.text = "Documento: ${documento.idDocumento}"
        holder.textViewStatusDocumento.text = "Status: ${documento.status}"

        // Aqui você pode processar o arquivo (por exemplo, convertê-lo de Base64 para ByteArray)
        documento.arquivo?.let { arquivoByteArray ->
            try {
                val arquivoBase64 = Base64.encodeToString(arquivoByteArray, Base64.DEFAULT)
                // Agora você tem o arquivo codificado em Base64 e pode exibi-lo ou processá-lo
                processaArquivo(arquivoBase64)
            } catch (e: Exception) {
                Log.e("DocumentosAdapter", "Erro ao processar arquivo: ${e.message}")
            }
        }
    }

    override fun getItemCount(): Int = documentosList.size

    // Função fictícia para processar o arquivo, você pode implementá-la conforme necessário
    private fun processaArquivo(arquivoBase64: String) {
        // Aqui você pode fazer o que for necessário com o arquivo, como salvar, enviar ou manipular.
        // Exemplo: Salvar em um arquivo local, enviar para o servidor ou outra lógica.
        Log.d("DocumentosAdapter", "Arquivo processado com sucesso. Tamanho: ${arquivoBase64} bytes")
    }

    class DocumentosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNomeDocumento: TextView = itemView.findViewById(R.id.textViewNomeDocumento)
        val textViewStatusDocumento: TextView = itemView.findViewById(R.id.textViewStatusDocumento)
    }
}