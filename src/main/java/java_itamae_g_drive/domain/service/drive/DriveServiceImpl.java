package java_itamae_g_drive.domain.service.drive;

import java.util.List;

import com.google.api.services.drive.model.File;

import java_itamae_g_auth.domain.model.AuthenticationAttribure;
import java_itamae_g_drive.domain.repository.drive.DriveRepository;
import java_itamae_g_drive.domain.repository.drive.DriveRepositoryImpl;

public class DriveServiceImpl implements DriveService {
    private final DriveRepository repository;

    public DriveServiceImpl(AuthenticationAttribure authAttr) {
        repository = new DriveRepositoryImpl(authAttr);
    }

    @Override
    public List<File> findByParentId(String parentId) throws Exception {
        List<File> result = repository.findByParentId(parentId);
        return result;
    }

    @Override
    public File findByFileId(String fileId) throws Exception {
        File result = repository.findByFileId(fileId);
        return result;
    }

    @Override
    public File upload(String localFilePath, String remoteParentId)
            throws Exception {
        File result = repository.upload(localFilePath, remoteParentId);
        return result;
    }

    @Override
    public File upload(String localFilePath) throws Exception {
        File result = repository.upload(localFilePath);
        return result;
    }

    @Override
    public java.io.File download(String localDirPath, String remoteFileId)
            throws Exception {
        java.io.File result = repository.download(localDirPath, remoteFileId);
        return result;
    }

    @Override
    public File update(String localFilePath, String remoteFileId)
            throws Exception {
        File result = repository.update(localFilePath, remoteFileId);
        return result;
    }

    @Override
    public void delete(String fileId) throws Exception {
        repository.delete(fileId);
    }

    @Override
    public String getDriveRootId() throws Exception {
        String rootId = repository.getDriveRootId();
        return rootId;
    }

}
