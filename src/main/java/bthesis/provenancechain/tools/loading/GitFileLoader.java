package bthesis.provenancechain.tools.loading;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.RepositoryFile;
import org.openprovenance.prov.interop.Formats;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The GitFileLoader class implements the FileLoader interface to load files from a Git repository.
 * Currently, this class throws an UnsupportedOperationException as Git file loading is not implemented.
 *
 * @author Tomas Zobac
 */
public class GitFileLoader implements IFileLoader {

    /**
     * Attempts to load files from a specified Git repository path.
     * Currently, this method throws an UnsupportedOperationException as it is not implemented.
     *
     * @param path The path to the directory in the Git repository from which files are to be loaded.
     * @return A list of File objects (not supported in the current implementation).
     * @throws UnsupportedOperationException When the method is invoked.
     */
    @Override
    public Document loadFile(String path) {
        URL testurl;
        InteropFramework intF = new InteropFramework();

        try {
            testurl = new URL(path);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        String[] pathSegments = testurl.getPath().split("/");
        StringBuilder filePath = new StringBuilder();
        for (int i = 6; i != pathSegments.length; i++) filePath.append("/").append(pathSegments[i]);
        filePath.deleteCharAt(0);

        try (GitLabApi gitLabApi = new GitLabApi(testurl.getProtocol() + "://" + testurl.getHost(), "usLuqR2Km1yvSAkssYSe")) {
            RepositoryFile file = gitLabApi.getRepositoryFileApi().getFile(pathSegments[1] + "/" + pathSegments[2], filePath.toString(), pathSegments[5]);
            return intF.deserialiseDocument(new ByteArrayInputStream(file.getDecodedContentAsBytes()), Formats.ProvFormat.PROVN);
        } catch (IOException | GitLabApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSupportedExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return false;
        }
        String ext = name.substring(lastIndexOf + 1);
        return SupportedExtensions.isSupported(ext);
    }


}
