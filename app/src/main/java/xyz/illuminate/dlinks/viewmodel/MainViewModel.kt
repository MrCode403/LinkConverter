package xyz.illuminate.dlinks.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.regex.Pattern

class LinkConverterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LinkConverterUiState())
    val uiState: StateFlow<LinkConverterUiState> = _uiState.asStateFlow()

    private val formatRegexes = arrayOf(
        Pattern.compile("https://drive\\.google\\.com/file/d/(?<id>.*?)/(?:edit|view)\\?usp=sharing"),
        Pattern.compile("https://drive\\.google\\.com/open\\?id=(?<id>.*)\$")
    )

    private val alphanumericRegex = Pattern.compile("^[\\w-]+\$")

    fun updateInputLink(link: String) {
        _uiState.value = _uiState.value.copy(
            inputLink = link,
            error = null
        )
    }

    fun selectLinkType(type: LinkType) {
        _uiState.value = _uiState.value.copy(
            selectedType = type,
            inputLink = "https://",
            outputLink = "",
            error = null
        )
    }

    fun convertLink() {
        val currentState = _uiState.value
        val link = currentState.inputLink

        // Validation
        if (!link.contains("http") || link.isEmpty() || link == "https://") {
            _uiState.value = currentState.copy(error = "Invalid Link")
            return
        }

        when (currentState.selectedType) {
            LinkType.MEDIAFIRE -> {
                if (!link.contains("mediafire.com")) {
                    _uiState.value = currentState.copy(error = "Invalid Mediafire Link")
                    return
                }
                convertMediafire(link)
            }

            LinkType.GOOGLE_DRIVE -> {
                if (!link.contains("drive.google.com")) {
                    _uiState.value = currentState.copy(error = "Invalid Google Drive Link")
                    return
                }
                val convertedLink = driveLink(link, null)
                _uiState.value = currentState.copy(
                    outputLink = convertedLink,
                    error = null,
                    showSuccessAnimation = true
                )
                viewModelScope.launch {
                    delay(2000)
                    _uiState.value = _uiState.value.copy(showSuccessAnimation = false)
                }
            }

            LinkType.DROPBOX -> {
                if (!link.contains("dl=0") || !link.contains("dropbox.com")) {
                    _uiState.value = currentState.copy(error = "Invalid Dropbox Link")
                    return
                }
                val convertedLink = convertDropbox(link)
                _uiState.value = currentState.copy(
                    outputLink = convertedLink,
                    error = null,
                    showSuccessAnimation = true
                )
                viewModelScope.launch {
                    delay(2000)
                    _uiState.value = _uiState.value.copy(showSuccessAnimation = false)
                }
            }
        }
    }

    private fun convertMediafire(link: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val convertedLink = withContext(Dispatchers.IO) {
                    val doc = Jsoup.connect(link).get()
                    val links = doc.select("a[href]")
                    links[7].attr("abs:href")
                }

                _uiState.value = _uiState.value.copy(
                    outputLink = convertedLink,
                    isLoading = false,
                    error = null,
                    showSuccessAnimation = true
                )

                delay(2000)
                _uiState.value = _uiState.value.copy(showSuccessAnimation = false)

            } catch (e: Exception) {
                Log.e("LinkConverter", "Error converting Mediafire link", e)
                _uiState.value = _uiState.value.copy(
                    outputLink = "",
                    isLoading = false,
                    error = "Failed to convert link: ${e.message}"
                )
            }
        }
    }

    private fun convertDropbox(link: String): String {
        return link.replace("dl=0", "dl=1")
    }

    private fun extractId(urlOrId: String): String {
        for (format in formatRegexes) {
            val matcher = format.matcher(urlOrId)
            if (matcher.find()) {
                return matcher.group("id") ?: "Invalid URL provided."
            }
        }

        if (alphanumericRegex.matcher(urlOrId).find()) {
            return urlOrId
        }

        return "Invalid URL provided."
    }

    private fun driveLink(urlOrId: String, apiKey: String?): String {
        val id = extractId(urlOrId.trim())
        val parsedKey = apiKey?.trim()

        if (parsedKey != null && !alphanumericRegex.matcher(parsedKey).matches()) {
            return "Invalid API key provided."
        }

        return if (parsedKey != null) {
            "https://www.googleapis.com/drive/v3/files/$id?alt=media&key=$parsedKey"
        } else {
            "https://drive.google.com/uc?export=download&id=$id"
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LinkConverterUiState(
    val selectedType: LinkType = LinkType.MEDIAFIRE,
    val inputLink: String = "https://",
    val outputLink: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val showSuccessAnimation: Boolean = false
)

enum class LinkType(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val color: androidx.compose.ui.graphics.Color
) {
    MEDIAFIRE(
        "Mediafire",
        "Convert mediafire link to direct downloadable link",
        xyz.illuminate.dlinks.R.drawable.mediafire_transparent,
        xyz.illuminate.dlinks.ui.theme.MediafireRed
    ),
    GOOGLE_DRIVE(
        "Google Drive",
        "Convert google drive link to direct downloadable link",
        xyz.illuminate.dlinks.R.drawable.gdrive_transparent,
        xyz.illuminate.dlinks.ui.theme.GoogleDriveBlue
    ),
    DROPBOX(
        "Dropbox",
        "Convert dropbox link to direct downloadable link",
        xyz.illuminate.dlinks.R.drawable.dropbox_transparent,
        xyz.illuminate.dlinks.ui.theme.DropboxBlue
    )
}
