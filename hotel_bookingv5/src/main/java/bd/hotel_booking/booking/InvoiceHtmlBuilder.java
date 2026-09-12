package bd.hotel_booking.booking;

import java.time.format.DateTimeFormatter;

/**
 * Builds a printable, downloadable invoice for a booking.
 *
 * <p>There's no PDF library in this project, and adding one just for a
 * single download button is a heavier change than this feature needs -
 * a self-contained HTML document downloads and opens fine in any browser,
 * and prints to PDF via the browser's own "Print &gt; Save as PDF" exactly
 * like the existing "Print booking" button already does. This keeps the
 * invoice real (built from the actual database row) without a new
 * dependency.</p>
 */
public final class InvoiceHtmlBuilder {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    private InvoiceHtmlBuilder() {
    }

    public static String build(Booking b) {
        String invoiceNo = "INV-" + b.getCreatedAt().getYear() + "-" + String.format("%06d", b.getId());
        long nights = java.time.temporal.ChronoUnit.DAYS.between(b.getCheckIn(), b.getCheckOut());
        String paid = b.getPaymentStatus() != null && b.getPaymentStatus().name().equals("PAID") ? "PAID" : "UNPAID";

        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\">"
                + "<title>Invoice " + invoiceNo + "</title>"
                + "<style>"
                + "body{font-family:Arial,Helvetica,sans-serif;color:#20242a;max-width:720px;margin:2rem auto;padding:0 1.5rem}"
                + "h1{font-size:1.4rem;margin-bottom:.1rem}"
                + ".muted{color:#6a7280;font-size:.85rem}"
                + ".row{display:flex;justify-content:space-between;margin:1.5rem 0;flex-wrap:wrap;gap:1rem}"
                + "table{width:100%;border-collapse:collapse;margin-top:1rem}"
                + "th,td{text-align:left;padding:.6rem .4rem;border-bottom:1px solid #e5e1d6}"
                + "th{font-size:.72rem;letter-spacing:.06em;text-transform:uppercase;color:#6a7280}"
                + ".total-row td{font-weight:700;font-size:1.05rem;border-top:2px solid #20242a;border-bottom:none}"
                + ".status{display:inline-block;padding:.25rem .7rem;border-radius:20px;font-size:.78rem;font-weight:700}"
                + ".paid{background:#e2f5ea;color:#1f8a4c}.unpaid{background:#fdecea;color:#c0392b}"
                + "@media print{body{margin:0;padding:1rem}}"
                + "</style></head><body>"
                + "<div class=\"row\">"
                + "<div><h1>Grand Meridian Resort</h1><div class=\"muted\">Cox's Bazar, Bangladesh</div></div>"
                + "<div style=\"text-align:right\"><h1>INVOICE</h1><div class=\"muted\">" + invoiceNo + "</div></div>"
                + "</div>"
                + "<div class=\"row\">"
                + "<div><div class=\"muted\">Billed to</div><b>" + esc(b.getGuestName()) + "</b><br>"
                + esc(b.getGuestEmail()) + "<br>" + esc(b.getGuestPhone()) + "</div>"
                + "<div style=\"text-align:right\"><div class=\"muted\">Issued</div>"
                + b.getCreatedAt().format(DATE_FMT) + "<br>"
                + "<span class=\"status " + (paid.equals("PAID") ? "paid" : "unpaid") + "\">" + paid + "</span></div>"
                + "</div>"
                + "<table><thead><tr><th>Description</th><th>Check-in</th><th>Check-out</th><th style=\"text-align:right\">Amount</th></tr></thead>"
                + "<tbody>"
                + "<tr><td>" + esc(b.getRoom().getName()) + " (Room " + esc(b.getRoom().getNumber()) + ") &middot; " + nights + " night" + (nights == 1 ? "" : "s") + "</td>"
                + "<td>" + b.getCheckIn().format(DATE_FMT) + "</td>"
                + "<td>" + b.getCheckOut().format(DATE_FMT) + "</td>"
                + "<td style=\"text-align:right\">" + money(b.getRoomTotal()) + "</td></tr>"
                + "<tr class=\"total-row\"><td colspan=\"3\">Total</td><td style=\"text-align:right\">" + money(b.getTotalAmount()) + "</td></tr>"
                + "</tbody></table>"
                + "<p class=\"muted\" style=\"margin-top:2rem\">Booking reference: HB-" + b.getCreatedAt().getYear() + "-" + String.format("%06d", b.getId())
                + " &middot; Guests: " + b.getAdults() + " adult" + (b.getAdults() == 1 ? "" : "s")
                + (b.getChildren() != null && b.getChildren() > 0 ? ", " + b.getChildren() + " child" + (b.getChildren() == 1 ? "" : "ren") : "") + "</p>"
                + "<p class=\"muted\">Thank you for staying with us.</p>"
                + "</body></html>";
    }

    private static String money(java.math.BigDecimal amount) {
        return "$" + (amount == null ? "0.00" : amount.setScale(2, java.math.RoundingMode.HALF_UP));
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
