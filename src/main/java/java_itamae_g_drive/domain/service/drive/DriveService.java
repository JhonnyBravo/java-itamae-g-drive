package java_itamae_g_drive.domain.service.drive;

import java.util.List;

import com.google.api.services.drive.model.File;

public interface DriveService {
    /**
     * 指定したディレクトリの配下に存在するファイル・ディレクトリの一覧を取得する。
     *
     * @param parentId
     *            検索対象とするディレクトリの file_id を指定する。
     * @return fileList ファイル・ディレクトリの一覧を返す。
     * @throws Exception
     *             {@link java.lang.Exception}
     */
    public List<File> findByParentId(String parentId) throws Exception;

    /**
     * file_id をキーにファイルまたはディレクトリを検索する。
     *
     * @param fileId
     *            検索対象とするファイルまたはディレクトリの file_id を指定する。
     * @return file ファイルまたはディレクトリを返す。
     * @throws Exception
     *             {@link java.lang.Exception}
     */
    public File findByFileId(String fileId) throws Exception;

    /**
     * ファイルまたはディレクトリをアップロードする。
     *
     * @param localFilePath
     *            アップロード対象とするファイルまたはディレクトリのパスを指定する。
     * @param remoteParentId
     *            格納先ディレクトリの file_id を指定する。
     * @return file ファイルまたはディレクトリを返す。
     * @throws Exception
     *             {@link java.lang.Exception}
     */
    public File upload(String localFilePath, String remoteParentId)
            throws Exception;

    /**
     * ファイルまたはディレクトリをアップロードする。
     *
     * @param localFilePath
     *            アップロード対象とするファイルまたはディレクトリのパスを指定する。
     * @return file ファイルまたはディレクトリを返す。
     * @throws Exception
     *             {@link java.lang.Exception}
     */
    public File upload(String localFilePath) throws Exception;

    /**
     * ファイルまたはディレクトリをダウンロードする。
     *
     * @param localDirPath
     *            ダウンロード先ディレクトリのパスを指定する。
     * @param remoteFileId
     *            ダウンロード対象とするファイルまたはディレクトリの file_id を指定する。
     * @return file ファイルまたはディレクトリを返す。
     * @throws Exception
     *             {@link java.lang.Exception}
     */
    public java.io.File download(String localDirPath, String remoteFileId)
            throws Exception;

    /**
     * ファイルまたはディレクトリを更新する。
     *
     * @param localFilePath
     *            更新対象とするファイルまたはディレクトリのパスを指定する。
     * @param remoteFileId
     *            更新先のファイルまたはディレクトリの file_id を指定する。
     * @return file ファイルまたはディレクトリを返す。
     * @throws Exception
     *             {@link java.lang.Exception}
     */
    public File update(String localFilePath, String remoteFileId)
            throws Exception;

    /**
     * ファイルまたはディレクトリを削除する。
     *
     * @param fileId
     *            削除対象とするファイルまたはディレクトリの file_id を指定する。
     * @throws Exception
     *             {@link java.lang.Exception}
     */
    public void delete(String fileId) throws Exception;

    /**
     * Google Drive のルートディレクトリの file_id を取得する。
     *
     * @return fileId ルートディレクトリの file_id を返す。
     * @throws Exception
     *             {@link java.lang.Exception}
     */
    public String getDriveRootId() throws Exception;
}
