### Files Description

- `./ (根目錄)`
    - `./MainActivity.java`: 登入後的主頁面Activity
    - `./ConnectionMysqlClass.java`: 連結MySql Class
    - `./MessageArrivedListener.java`: MQTT訊息接收Listener
    - `./MqttHandler.java`: MQTT Handler
    - `./SharedViewModel.java`: Fragment間共享資料
    - `./SplashActivity.java`: 啟動畫面Activity
    - `./UserClass.java`: 使用者Class
      
> [!IMPORTANT]
> MessageArrivedListener.java, MqttHandler.java, SharedViewModel.java, SplashActivity.java皆不影響Class Diagram及Sequence Diagram

- `./ui/`
  - `./ui/changeUserInfo/ChangeUserInfoFragment.java`: 更改使用者資訊
  - `./ui/feed/FeedFragment.java`: 選擇手動或自動餵食
  - `./ui/feedAutomatic/FeedAutomaticFragment.java`: 自動餵食
  - `./ui/feeding/FeedingFragment.java`: 使用者手動餵食按下submit按鈕後，前端顯示機器餵食狀態之畫面
  - `./ui/feedManual/FeedManualFragment.java`: 手動餵食
  - `./ui/healthcare/HealthCareFragment.java`: 寵物衛教
  - `./ui/home/HomeFragment.java`: 首頁
  - `./ui/login/LoginActivity.java`: 登入Activity
  - `./ui/monitor/MonitorFragment.java`: 監控
  - `./ui/petInfo/PetInfoFragment.java`: 註冊寵物
  - `./ui/register/RegisterFragment.java`: 註冊使用者
  - `./ui/settings/SettingsFragment.java`: 設定
