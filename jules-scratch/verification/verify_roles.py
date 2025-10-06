import re
from playwright.sync_api import sync_playwright, Page, expect

def login(page: Page, username, password):
    """Helper function to log in."""
    page.goto("http://localhost:3000/login", timeout=60000)
    expect(page.get_by_role("heading", name="Welcome Back")).to_be_visible(timeout=15000)
    page.get_by_label("Username").fill(username)
    page.get_by_label("Password").fill(password)
    page.get_by_role("button", name="Sign In").click()
    expect(page.get_by_role("heading", name=re.compile(f"Welcome, {username}"))).to_be_visible(timeout=15000)

def logout(page: Page):
    """Helper function to log out."""
    page.get_by_role("button", name="Logout").click()
    expect(page).to_have_url("http://localhost:3000/login")

def test_user_flow(page: Page, username, password, screenshot_name):
    """Generic function to test a user flow."""
    login(page, username, password)

    page.screenshot(path=f"jules-scratch/verification/{screenshot_name}")

    if username == "admin":
        expect(page.get_by_role("link", name="Users")).to_be_visible()
    else:
        expect(page.get_by_role("link", name="Users")).not_to_be_visible()

    logout(page)

def main():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        try:
            test_user_flow(page, "admin", "admin123", "superuser_dashboard.png")
            test_user_flow(page, "station_commander", "admin123", "policestation_dashboard.png")
            test_user_flow(page, "officer_001", "admin123", "officer_dashboard.png")
        finally:
            browser.close()

if __name__ == "__main__":
    main()