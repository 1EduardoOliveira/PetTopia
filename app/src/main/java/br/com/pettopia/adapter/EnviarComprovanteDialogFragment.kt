package br.com.pettopia.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import br.com.pettopia.databinding.FragmentEnviarComprovanteBinding
import br.com.pettopia.viewModel.ClienteViewModel

class EnviarComprovanteDialogFragment(private val animalId: Long) : DialogFragment() {

    private lateinit var binding: FragmentEnviarComprovanteBinding
    private lateinit var viewModel: ClienteViewModel
    private var selectedFileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnviarComprovanteBinding.inflate(inflater, container, false)

        // Inicializa o ViewModel
        viewModel = ViewModelProvider(requireActivity())[ClienteViewModel::class.java]

        Log.d("EnviarComprovanteDialogFragment", "Animal ID: $animalId")

        // Configura o botão de enviar
        binding.btnEnviar.setOnClickListener {
            selectedFileUri?.let { fileUri ->
                Log.d("EnviarComprovanteDialogFragment", "Arquivo selecionado: $fileUri")
                // Chama a função de envio do comprovante no ViewModel
                viewModel.enviarComprovanteRenda(animalId, fileUri, requireContext())
                Log.d("EnviarComprovanteDialogFragment", "Envio do comprovante iniciado")
                Log.d("EnviarComprovanteDialogFragment", "Animal ID: $animalId")
                Log.d("EnviarComprovanteDialogFragment", "Arquivo URI: $fileUri")
                Log.d("EnviarComprovanteDialogFragment", "Context: ${requireContext()}")
                Toast.makeText(requireContext(), "Comprovante enviado com sucesso", Toast.LENGTH_SHORT).show()
                dismiss() // Fecha o DialogFragment após o envio
            } ?: Toast.makeText(requireContext(), "Selecione um arquivo", Toast.LENGTH_SHORT).show()
            Log.d("EnviarComprovanteDialogFragment", "Arquivo não selecionado")
        }


        // Configura o clique para selecionar um arquivo
        binding.fileInput.setOnClickListener {

            Log.d("EnviarComprovanteDialogFragment", "Botão de seleção de arquivo clicado")
            // Aqui você pode abrir um seletor de arquivos (exemplo simples de escolha de arquivo)
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*" // Define o tipo de arquivo permitido
                Log.d("EnviarComprovanteDialogFragment", "Intent criada")
            }
            startActivityForResult(intent, REQUEST_CODE_FILE)
            Log.d("EnviarComprovanteDialogFragment", "Atividade de seleção de arquivo iniciada")
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_FILE && resultCode == Activity.RESULT_OK) {
            // Verifica se o usuário selecionou um arquivo
            data?.data?.let { uri ->
                selectedFileUri = uri
                // Atualiza a UI (se necessário)
                binding.fileInput.setText(uri.toString()) // Mostra o caminho do arquivo
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_FILE = 1
    }
}
