package java_itamae_g_drive.domain.repository.drive;

import java.util.List;

import com.google.api.services.drive.model.File;

public interface DriveRepository {
    /**
     * Google Drive 上に存在する全てのファイル・ディレクトリの一覧を取得する。
     *
     * @return fileList ファイル・ディレクトリの一覧を返す。
     * @throws Exception
     *             {@link java.lang.Exception}
     */
    public List<File> findAll() throws Exception;

    /**
     * クエリに記述した検索条件に該当するファイル・ディレクトリの一覧を取得する。
     *
     * @param query
     *            検索対象とするクエリを指定する。
     * @return fileList ファイル・ディレクトリの一覧を返す。
     * @throws Exception
     *             {@link java.lang.Exception}
     */
    public List<File> findByQuery(String query) throws Exception;

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
     * @return file アップロードされたファイルまたはディレクトリを返す。
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
     * @return file アップロードされたファイルまたはディレクトリを返す。
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
     * @return file ダウンロードされたファイルまたはディレクトリを返す。
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
     *            更新対象とするファイルまたはディレクトリの file_id を指定する。
     * @return file 更新されたファイルまたはディレクトリを返す。
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
     * Google Drive のルートディレクトリの ID を取得する。
     *
     * @return file_id ルートディレクトリの file_id を返す。
     * @throws Exception
     *             {@link java.lang.Exception}
     */
    public String getDriveRootId() throws Exception;
}
