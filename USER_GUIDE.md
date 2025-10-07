# NISIRCOP-LE User Guide
## Role-Based System Usage

---

## System Access

**System URL**: http://localhost:8080 (when deployed)

**Default User Accounts**:

| Role | Username | Password | Purpose |
|------|----------|----------|---------|
| Administrator | `admin` | `admin123` | System-wide management |
| Station Commander | `station_commander` | `admin123` | Station management |
| Field Officer | `officer_001` | `admin123` | Incident reporting |

⚠️ **Security Note**: Change these passwords immediately in production!

---

## 📱 Officer Guide (Field Officer)

**Role**: OFFICER  
**Primary Tasks**: Report incidents, view own reports  
**Access Level**: Limited to personal data

### What You Can Do

✅ Report new crime incidents from the field  
✅ View all incidents you've reported  
✅ Update your own incident reports  
✅ Delete your own incident reports  
✅ Update your personal profile  

❌ Cannot create user accounts  
❌ Cannot view other officers' incidents  
❌ Cannot access system administration  

### How to Login

1. Open the application
2. Enter credentials:
   - Username: `officer_001`
   - Password: `admin123`
3. Click "Login"
4. You will be redirected to the Officer Dashboard

### How to Report an Incident

#### Step 1: Start New Report
- Click "New Incident" button
- Or press "Quick Report" from dashboard

#### Step 2: Fill in Incident Details

**Title** (Required)
```
Example: "Smartphone Theft at Market"
```
Brief, clear description (max 255 characters)

**Incident Type** (Required)
Choose from:
- Theft
- Burglary
- Assault
- Vandalism
- Drug Offense
- Traffic Incident
- Homicide
- Robbery
- Domestic Violence
- Other

**Priority Level** (Required)
- **CRITICAL**: Immediate threat to life
- **HIGH**: Serious crime, needs prompt response
- **MEDIUM**: Standard response required
- **LOW**: Minor incident, routine handling

**Location** (Required)
- Latitude: `9.0320`
- Longitude: `38.7469`
- *Or use GPS auto-capture*

**Description** (Optional)
```
Detailed information about the incident:
- What happened
- When it occurred
- Who was involved
- What was taken/damaged
- Witness information
- Suspect description
```

#### Step 3: Validate Location

⚠️ **Important**: Your location MUST be within your assigned patrol boundary

- System will automatically check if coordinates are valid
- If outside boundary: Error message shown
- If inside boundary: Incident will be created

#### Step 4: Submit Report

- Click "Submit Incident"
- Confirmation message displayed
- Incident ID generated
- Report saved to database

### View Your Incidents

1. Go to "My Incidents" section
2. See list of all your reports:
   - Incident ID
   - Title
   - Type
   - Priority
   - Date/Time
   - Status

3. Click any incident to see full details

### Update an Incident

1. Go to "My Incidents"
2. Click the incident you want to update
3. Click "Edit" button
4. Modify any field except:
   - Reporter (always you)
   - Incident ID (system generated)
5. Click "Save Changes"

### Delete an Incident

1. Go to "My Incidents"
2. Click the incident you want to delete
3. Click "Delete" button
4. Confirm deletion
5. Incident permanently removed

### Update Your Profile

1. Click your name (top right)
2. Select "Profile Settings"
3. You can update:
   - Phone number
   - Email address
   - Password
4. Click "Save"

⚠️ **Cannot Change**:
- Username
- Role
- Badge Number
- Assigned Boundary

### Dashboard Overview

**My Statistics**:
- Total incidents reported
- This week's incidents
- This month's incidents
- Incident breakdown by type

**Recent Activities**:
- Last 10 incidents you reported
- Quick access to edit/view

**Quick Actions**:
- New Incident (button)
- View All My Incidents
- Update Profile

### Mobile Usage Tips

📱 **Best Practices**:
- Enable GPS for auto-location
- Take photos (when feature available)
- Report immediately for accuracy
- Check network connection before submitting
- Save draft if connection lost

---

## 🏛️ Police Station Commander Guide

**Role**: POLICE_STATION  
**Primary Tasks**: Manage officers, monitor station incidents  
**Access Level**: Station-wide data

### What You Can Do

✅ Create new OFFICER accounts for your station  
✅ View all incidents from your officers  
✅ Update officer information  
✅ Delete officer accounts  
✅ Edit incidents reported by your officers  
✅ Delete incidents from your officers  
✅ View station-level analytics  
✅ Report incidents (within boundary)  

❌ Cannot create SUPER_USER or POLICE_STATION accounts  
❌ Cannot view incidents from other stations  
❌ Cannot modify other stations' officers  

### How to Login

1. Open the application
2. Enter credentials:
   - Username: `station_commander`
   - Password: `admin123`
3. Click "Login"
4. You will be redirected to Station Dashboard

### Dashboard Overview

**Station Statistics**:
- Total officers under your command
- Total incidents (all your officers)
- Active incidents
- Resolved incidents
- This week's activity

**Officer Performance**:
- Top reporting officers
- Response times
- Incident quality scores

**Geographic Map**:
- Station boundary displayed
- All incidents plotted
- Heatmap of crime hotspots

### Managing Officers

#### Create New Officer

1. Go to "Manage Officers" section
2. Click "Add New Officer"
3. Fill in officer details:

```
Username: officer_002
Password: [secure password]
Email: officer002@nisircop.le
First Name: John
Last Name: Doe
Phone: +251-11-555-1234
Badge Number: OFF-002
```

4. Assign patrol boundary (optional):
   - Draw on map, or
   - Enter coordinates
5. Click "Create Officer"
6. Officer can now login

#### View All Officers

1. Go to "Manage Officers"
2. See list of all your officers:
   - Name
   - Badge Number
   - Status (Active/Inactive)
   - Total Incidents Reported
   - Last Login
3. Click any officer for details

#### Update Officer Information

1. Go to "Manage Officers"
2. Click the officer to update
3. Click "Edit"
4. Modify:
   - Contact information
   - Badge number
   - Assigned boundary
   - Active status
5. Click "Save"

#### Deactivate Officer

1. Go to "Manage Officers"
2. Click the officer
3. Click "Deactivate"
4. Officer cannot login (data preserved)

#### Delete Officer

1. Go to "Manage Officers"
2. Click the officer
3. Click "Delete"
4. ⚠️ Confirm deletion
5. Officer and associated data removed

### Managing Incidents

#### View All Station Incidents

1. Go to "All Incidents"
2. View incidents from all your officers
3. Filter by:
   - Date range
   - Officer
   - Incident type
   - Priority
   - Status

#### View Specific Officer's Incidents

1. Go to "Manage Officers"
2. Click an officer
3. See "Incidents" tab
4. View all incidents from that officer

#### Edit Any Station Incident

1. Find the incident (All Incidents or Officer view)
2. Click the incident
3. Click "Edit"
4. Modify any field
5. Click "Save"

**Use Cases**:
- Correct errors in reports
- Add additional information
- Update priority
- Change incident type

#### Delete Station Incident

1. Find the incident
2. Click the incident
3. Click "Delete"
4. Confirm deletion
5. Incident removed

**Use Cases**:
- Remove duplicate reports
- Delete test/training incidents
- Remove accidentally created reports

### Station Analytics

#### View Crime Statistics

1. Go to "Analytics" section
2. See station-specific data:
   - Incident count by type
   - Incident count by priority
   - Temporal patterns (daily/weekly/monthly)
   - Geographic distribution

#### Crime Heatmap

1. Go to "Map View"
2. See:
   - Station boundary (blue outline)
   - All incidents (color-coded markers)
   - Crime density heatmap
3. Click any marker for incident details

#### Reports

1. Go to "Reports" section
2. Generate:
   - Weekly station report
   - Monthly statistics
   - Officer performance report
   - Incident type analysis
3. Export as PDF or CSV

### Best Practices for Station Commanders

✅ **Daily Tasks**:
- Review new incidents from previous day
- Check officer activity
- Monitor high-priority incidents
- Respond to alerts

✅ **Weekly Tasks**:
- Generate weekly report
- Review officer performance
- Update incident statuses
- Check boundary compliance

✅ **Monthly Tasks**:
- Comprehensive station analysis
- Officer evaluations
- Update patrol boundaries
- System maintenance

---

## 👨‍💼 Administrator Guide (SUPER_USER)

**Role**: SUPER_USER  
**Primary Tasks**: System-wide management, create stations  
**Access Level**: Full system access

### What You Can Do

✅ **Everything** - Full system access  
✅ Create SUPER_USER accounts (other admins)  
✅ Create POLICE_STATION accounts  
✅ Create OFFICER accounts  
✅ View ALL incidents from all users  
✅ Edit/delete any incident  
✅ Update/delete any user  
✅ Access system-wide analytics  
✅ Report incidents anywhere (no boundary restriction)  
✅ Configure system settings  

### How to Login

1. Open the application
2. Enter credentials:
   - Username: `admin`
   - Password: `admin123`
3. Click "Login"
4. You will be redirected to Admin Dashboard

### Admin Dashboard

**System Overview**:
- Total users (all roles)
- Total police stations
- Total officers
- Total incidents (all-time)
- Active incidents
- System health status

**National Crime Map**:
- All incidents from all stations
- National crime heatmap
- Station boundaries displayed
- Real-time updates

**Recent Activity**:
- Latest incidents (all stations)
- New user registrations
- System alerts
- Failed login attempts

### User Management

#### Create Police Station Account

1. Go to "User Management"
2. Click "Add Police Station"
3. Fill in details:

```
Username: addis_station_01
Password: [secure password]
Email: station01@nisircop.le
Station Name: Addis Ababa Central Station
Commander Name: [Name]
Phone: +251-11-XXX-XXXX
Badge Number: STA-001
```

4. Assign station boundary:
   - Draw polygon on map
   - Or upload GeoJSON
   - Or enter coordinates
5. Click "Create Station"

#### Create Administrator Account

1. Go to "User Management"
2. Click "Add Administrator"
3. Fill in details (similar to above)
4. Set role: SUPER_USER
5. Click "Create"

**Security Best Practice**:
- Limit number of admin accounts
- Use strong passwords
- Enable 2FA (when available)
- Audit admin activities

#### View All Users

1. Go to "User Management"
2. See complete user list:
   - Filter by role
   - Search by name/badge
   - Sort by creation date
   - See active/inactive status

#### Hierarchy View

```
You (Admin)
├── Station Commander 1
│   ├── Officer 1
│   ├── Officer 2
│   └── Officer 3
├── Station Commander 2
│   ├── Officer 4
│   └── Officer 5
└── Direct Officers (rare)
```

#### Deactivate/Delete Users

1. Find user in "User Management"
2. Click the user
3. Options:
   - **Deactivate**: User cannot login, data preserved
   - **Delete**: Permanently remove user and data
4. Confirm action

### System-Wide Incident Management

#### View All Incidents

1. Go to "All Incidents" (Admin view)
2. See every incident in system
3. Advanced filtering:
   - By station
   - By officer
   - By date range
   - By type
   - By priority
   - By geographic region

#### National Crime Map

1. Go to "Crime Map"
2. Interactive map showing:
   - All station boundaries (color-coded)
   - All incidents (clustered markers)
   - Heat map overlay
   - Density visualization

3. Map Controls:
   - Zoom in/out
   - Toggle layers (boundaries, markers, heatmap)
   - Time slider (see incidents over time)
   - Filter by type/priority

#### Edit/Delete Any Incident

- Same process as Station Commander
- But with access to ALL incidents
- Use carefully - powerful permission

### National Analytics

#### Dashboard Metrics

**Crime Overview**:
- Total incidents (national)
- Incidents this week/month/year
- Crime rate trends
- Most common crime types

**Geographic Analysis**:
- Hotspot identification
- Station comparison
- Urban vs rural analysis
- Border area monitoring

**Temporal Patterns**:
- Daily patterns (time of day)
- Weekly patterns (day of week)
- Monthly trends
- Seasonal analysis

#### Advanced Reports

1. Go to "Advanced Analytics"
2. Generate reports:
   - National crime report
   - Inter-station comparison
   - Resource allocation analysis
   - Predictive analysis (when ML enabled)

3. Export options:
   - PDF report
   - Excel spreadsheet
   - CSV data
   - JSON for further analysis

### System Configuration

#### General Settings

1. Go to "System Settings"
2. Configure:
   - System name and logo
   - Default time zone
   - Date/time formats
   - Language settings
   - Email notifications

#### Security Settings

1. Go to "Security Settings"
2. Configure:
   - Password requirements (length, complexity)
   - Session timeout duration
   - JWT token expiration
   - Failed login attempts limit
   - IP whitelist/blacklist

#### Geographic Settings

1. Go to "Geographic Settings"
2. Configure:
   - Default map center
   - Default zoom level
   - Coordinate system (WGS84, etc.)
   - Boundary validation strictness

### System Monitoring

#### Service Health

1. Go to "System Health"
2. Monitor all microservices:
   - Discovery Server ✅
   - API Gateway ✅
   - Auth Service ✅
   - User Service ✅
   - Geographic Service ✅
   - Incident Service ✅
   - Analytics Service ✅

3. For each service see:
   - Status (UP/DOWN)
   - Response time
   - Memory usage
   - CPU usage
   - Last restart

#### Logs and Audit Trail

1. Go to "System Logs"
2. View:
   - Application logs
   - Error logs
   - Access logs
   - Audit trail (who did what, when)

3. Search and filter:
   - By date range
   - By service
   - By user
   - By action type
   - By error level

#### User Activity

1. Go to "User Activity"
2. See:
   - Currently logged in users
   - Recent login history
   - Failed login attempts
   - Active sessions
   - User actions

### Database Management

#### Backup and Restore

1. Go to "Database"
2. Create backup:
   - Full database backup
   - Incremental backup
   - Specific table backup

3. Restore from backup:
   - Select backup file
   - Choose restore point
   - Confirm restoration

#### Database Statistics

- Total records
- Storage usage
- Growth trends
- Query performance

### Best Practices for Administrators

✅ **Daily Tasks**:
- Check system health dashboard
- Review system alerts
- Monitor active sessions
- Check backup status

✅ **Weekly Tasks**:
- Review security logs
- Check user activity
- Generate weekly report
- Update system if needed

✅ **Monthly Tasks**:
- Comprehensive system audit
- Review and update settings
- Analyze national crime trends
- Plan system improvements

✅ **Security Practices**:
- Regularly change admin password
- Review user permissions
- Audit station boundaries
- Check for suspicious activity
- Keep system updated

---

## 🔐 Security Best Practices (All Roles)

### Password Security

✅ **Do**:
- Use strong passwords (12+ characters)
- Mix uppercase, lowercase, numbers, symbols
- Use unique password for this system
- Change password regularly (every 90 days)
- Use password manager

❌ **Don't**:
- Share your password
- Write password down
- Use common passwords (admin123, password, etc.)
- Reuse passwords from other sites

### Session Security

✅ **Do**:
- Logout when done
- Lock screen if stepping away
- Use secure networks
- Clear browser cache on shared computers

❌ **Don't**:
- Leave session open unattended
- Use on public/untrusted WiFi without VPN
- Save password in browser on shared devices

### Data Security

✅ **Do**:
- Double-check information before submitting
- Report suspicious activity
- Follow data classification guidelines
- Use encryption for sensitive communications

❌ **Don't**:
- Share incident details publicly
- Take screenshots of sensitive data
- Forward confidential information
- Access system from compromised devices

---

## 📞 Support and Help

### Getting Help

**Technical Issues**:
- Email: support@nisircop.le
- Phone: +251-11-XXX-XXXX
- Help Desk: Available 24/7

**Training**:
- Video tutorials available at: [URL]
- User manual: This document
- In-person training sessions: Contact admin

### Common Issues and Solutions

#### Cannot Login

**Problem**: Invalid credentials error  
**Solution**:
1. Check username spelling
2. Verify password (case-sensitive)
3. Check Caps Lock is off
4. Contact admin to reset password

#### Incident Location Invalid

**Problem**: "Outside boundary" error  
**Solution**:
1. Verify GPS coordinates are correct
2. Check if you're within assigned boundary
3. Contact station commander to adjust boundary
4. If SUPER_USER, no boundary restriction

#### Cannot Create User

**Problem**: Permission denied  
**Solution**:
1. Verify your role:
   - OFFICER: Cannot create users
   - POLICE_STATION: Can create OFFICER only
   - SUPER_USER: Can create any role
2. Contact admin if you need different permissions

#### Page Loading Slowly

**Problem**: Slow performance  
**Solution**:
1. Check internet connection
2. Clear browser cache
3. Close other browser tabs
4. Contact support if persists

---

## 📱 Mobile App Usage (Future)

When the mobile application is released:

### Features

- Native iOS and Android apps
- Offline incident reporting
- GPS auto-capture
- Photo attachments
- Push notifications
- Voice-to-text reporting

### Download

- App Store (iOS): [Link]
- Play Store (Android): [Link]

---

## Appendix: Quick Reference Cards

### Officer Quick Reference

```
LOGIN: officer_001 / admin123

REPORT INCIDENT:
1. New Incident button
2. Fill: Title, Type, Priority
3. GPS auto-capture
4. Add description
5. Submit

VIEW MY INCIDENTS:
Dashboard → My Incidents

UPDATE INCIDENT:
My Incidents → Click incident → Edit

Location must be within boundary!
```

### Station Commander Quick Reference

```
LOGIN: station_commander / admin123

CREATE OFFICER:
Manage Officers → Add Officer
Fill form → Assign boundary → Create

VIEW STATION INCIDENTS:
Dashboard → All Incidents
(Shows all your officers' incidents)

STATION ANALYTICS:
Dashboard → Analytics
See crime statistics and maps
```

### Administrator Quick Reference

```
LOGIN: admin / admin123

CREATE STATION:
User Management → Add Police Station
Fill form → Draw boundary → Create

VIEW ALL INCIDENTS:
All Incidents (admin view)
National map available

SYSTEM HEALTH:
System → Health Dashboard
Monitor all microservices

Full access to everything!
```

---

**Document Version**: 1.0  
**Last Updated**: 2025-10-07  
**For**: NISIRCOP-LE Platform

**Questions?** Contact your system administrator or support team.
