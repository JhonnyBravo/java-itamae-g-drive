package java_itamae_g_drive.domain.repository.drive;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java_itamae_g_auth.domain.model.AuthenticationAttribure;

public class DriveRepositoryImpl implements DriveRepository {
    private final Drive drive;
    private final Logger logger;

    public DriveRepositoryImpl(AuthenticationAttribure authAttr) {
        logger = LoggerFactory.getLogger(this.getClass());

        Drive.Builder builder = new Drive.Builder(authAttr.getHttpTransport(),
                authAttr.getJsonFactory(), authAttr.getCredential());
        drive = builder.build();
    }

    @Override
    public List<File> findAll() throws Exception {
        logger.info("ファイル一覧を取得しています......");

        FileList fileList = drive.files().list()
                .setFields("files(id,name,mimeType,parents)").execute();
        List<File> result = fileList.getFiles();

        return result;
    }

    @Override
    public List<File> findByQuery(String query) throws Exception {
        logger.info("ファイル一覧を取得しています......");
        logger.info(String.format("query: %s", query));

        FileList fileList = drive.files().list()
                .setFields("files(id,name,mimeType,parents)").setQ(query)
                .execute();
        List<File> result = fileList.getFiles();

        return result;
    }

    @Override
    public List<File> findByParentId(String parentId) throws Exception {
        String query = String.format("'%s' in parents", parentId);
        List<File> result = findByQuery(query);
        return result;
    }

    @Override
    public File findByFileId(String fileId) throws Exception {
        logger.info("ファイルを検索しています......");
        File file = drive.files().get(fileId)
                .setFields("id,name,mimeType,parents").execute();
        return file;
    }

    @Override
    public File upload(String localFilePath) throws Exception {
        String rootId = getDriveRootId();
        File remoteFile = this.upload(localFilePath, rootId);
        return remoteFile;
    }

    @Override
    public File upload(String localFilePath, String remoteParentId)
            throws Exception {
        // ファイル名を設定する。
        File metaFile = new File();

        java.io.File localFile = new java.io.File(localFilePath);
        metaFile.setName(localFile.getName());
        logger.info(
                String.format("%s をアップロードしています......", localFile.getName()));

        // 親ディレクトリを設定する。
        List<String> parentList = new ArrayList<>();
        parentList.add(remoteParentId);
        metaFile.setParents(parentList);

        File remoteFile = null;

        if (localFile.isDirectory()) {
            // localFile がディレクトリである場合は mimeType を設定する。
            metaFile.setMimeType("application/vnd.google-apps.folder");
            remoteFile = drive.files().create(metaFile).execute();
        } else {
            // localFile がディレクトリではない場合はファイルの内容を設定する。
            FileContent fileContent = new FileContent(null, localFile);
            remoteFile = drive.files().create(metaFile, fileContent).execute();
        }

        logger.info("id: " + remoteFile.getId());
        logger.info("name: " + remoteFile.getName());
        logger.info("mime_type: " + remoteFile.getMimeType());
        logger.info("path: " + localFile.getCanonicalPath());

        return remoteFile;
    }

    @Override
    public java.io.File download(String localDirPath, String remoteFileId)
            throws Exception {
        File remoteFile = findByFileId(remoteFileId);
        java.io.File localFile = new java.io.File(localDirPath,
                remoteFile.getName());
        logger.info(
                String.format("%s をダウンロードしています......", remoteFile.getName()));

        if (remoteFile.getMimeType()
                .equals("application/vnd.google-apps.folder")) {
            localFile.mkdirs();
        } else {
            try (FileOutputStream stream = new FileOutputStream(localFile)) {
                drive.files().get(remoteFileId)
                        .executeMediaAndDownloadTo(stream);
            }
        }

        logger.info("id: " + remoteFile.getId());
        logger.info("name: " + remoteFile.getName());
        logger.info("mime_type: " + remoteFile.getMimeType());
        logger.info("path: " + localFile.getCanonicalPath());

        return localFile;
    }

    @Override
    public File update(String localFilePath, String remoteFileId)
            throws Exception {
        java.io.File localFile = new java.io.File(localFilePath);
        File remoteFile = findByFileId(remoteFileId);

        File metaFile = new File();
        metaFile.setName(localFile.getName());
        metaFile.setMimeType(remoteFile.getMimeType());

        List<String> parentsList = remoteFile.getParents();
        String parents = String.join(",", parentsList);

        logger.info(String.format("%s を更新しています......", remoteFile.getName()));

        File result = null;

        if (localFile.isDirectory()) {
            result = drive.files().update(remoteFileId, metaFile)
                    .setAddParents(parents).execute();
        } else {
            FileContent fileContent = new FileContent(null, localFile);
            result = drive.files().update(remoteFileId, metaFile, fileContent)
                    .setAddParents(parents).execute();
        }

        logger.info("id: " + result.getId());
        logger.info("name: " + result.getName());
        logger.info("mime_type: " + result.getMimeType());
        logger.info("path: " + localFile.getCanonicalPath());

        return result;
    }

    @Override
    public void delete(String fileId) throws Exception {
        File file = findByFileId(fileId);
        logger.info(String.format("%s を削除しています......", file.getName()));
        drive.files().delete(fileId).execute();

        logger.info("id: " + file.getId());
        logger.info("name: " + file.getName());
        logger.info("mime_type: " + file.getMimeType());
    }

    @Override
    public String getDriveRootId() throws Exception {
        String id = findByFileId("root").getId();
        return id;
    }

}
