# Privacy Policy

MonadBox is an open-source Android client. The app is provided as-is and does not intentionally collect personally identifiable information.

## Data Collection

- We do not bundle third-party analytics SDKs.
- We do not bundle third-party crash reporting SDKs.
- We do not bundle cloud update SDKs that upload your app usage data.
- Runtime logs are stored locally on your device unless you explicitly export or share them.

## Permission Usage

MonadBox requests only permissions required for local functionality:

- `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`:
  Required for proxy runtime networking, profile download, and network state checks.
- `POST_NOTIFICATIONS`:
  Used to display runtime/service status notifications.
- `CAMERA`:
  Optional. Used only when you choose QR code import.
- `GET_INSTALLED_APPS`, `QUERY_ALL_PACKAGES`:
  Used for access-control and RootTun app filtering (include/exclude app traffic). Package lists are processed locally.
- Foreground service permissions:
  Required by Android for running VPN/proxy background services.

MonadBox does not upload your installed app list, profile files, or runtime logs to an app-owned cloud backend.

## File Access And Sharing

- Profile import/export is user-triggered.
- File sharing is performed through Android system sharing (`FileProvider`) and only for files you explicitly choose to export.

## Cookies

MonadBox itself does not use web cookies. If you open third-party websites from links in the app, those websites may apply their own cookie and privacy policies.

## Security

We use reasonable measures to protect local data, but no system can guarantee absolute security.

## Children’s Privacy

MonadBox is not specifically directed to children under 13, and we do not knowingly collect personal data from children.

## Changes To This Policy

This policy may be updated over time. Updates are effective after publication in this repository and app resources.

## Contact

If you have questions or suggestions about this Privacy Policy, please contact the project maintainers through the repository issue tracker.
