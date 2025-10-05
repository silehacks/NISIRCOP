from playwright.sync_api import sync_playwright, expect
import time

def run(playwright):
    browser = playwright.chromium.launch(headless=True)
    context = browser.new_context()
    page = context.new_page()

    try:
        print("Navigating to login page...")
        page.goto("http://localhost:3000/login", timeout=60000)

        print("Filling in login credentials...")
        page.get_by_placeholder("Username").fill("admin")
        page.get_by_placeholder("Password").fill("admin123")

        print("Clicking login button...")
        page.get_by_role("button", name="Sign In").click()

        print("Waiting for dashboard...")
        expect(page.get_by_role("heading", name="Welcome, admin")).to_be_visible(timeout=30000)
        page.screenshot(path="jules-scratch/verification/01_dashboard.png")
        print("Dashboard screenshot taken.")

        print("Navigating to Incidents page...")
        page.get_by_role("link", name="Incidents").click()
        expect(page.get_by_role("heading", name="Incidents")).to_be_visible()
        page.wait_for_timeout(1000) # wait for data to load
        page.screenshot(path="jules-scratch/verification/02_incidents.png")
        print("Incidents page screenshot taken.")

        print("Navigating to Analytics page...")
        page.get_by_role("link", name="Analytics").click()
        expect(page.get_by_role("heading", name="Analytics Dashboard")).to_be_visible()
        page.wait_for_timeout(3000)  # Wait for charts to render
        page.screenshot(path="jules-scratch/verification/03_analytics.png")
        print("Analytics page screenshot taken.")

        print("Navigating to Users page...")
        page.get_by_role("link", name="Users").click()
        expect(page.get_by_role("heading", name="User Management")).to_be_visible()
        page.wait_for_timeout(1000) # wait for data to load
        page.screenshot(path="jules-scratch/verification/04_users.png")
        print("Users page screenshot taken.")

        print("Logging out...")
        page.get_by_role("button", name="Logout").click()
        expect(page.get_by_role("heading", name="Welcome Back")).to_be_visible()
        page.screenshot(path="jules-scratch/verification/05_logout.png")
        print("Logout screenshot taken.")

        print("Verification script completed successfully!")

    except Exception as e:
        print(f"An error occurred: {e}")
        page.screenshot(path="jules-scratch/verification/error.png")
    finally:
        browser.close()

with sync_playwright() as playwright:
    run(playwright)