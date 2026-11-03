"""Render the system-context figure from explicit SVG geometry."""
from pathlib import Path
from html import escape
import pymupdf

ROOT = Path(__file__).resolve().parents[1]
TARGET = ROOT / "docs" / "assets" / "system-context.png"


def build():
    nodes = [
        (40, 55, "Pessoa desenvolvedora"), (40, 180, "Revisor técnico"),
        (40, 305, "Cliente de consulta"), (360, 95, "Angular workspace"),
        (360, 305, "Consultas MCP"), (690, 180, "Spring Boot API"),
        (1000, 55, "PostgreSQL"), (1000, 180, "Prometheus"),
        (690, 390, "Pipeline CI/CD"),
    ]
    parts = ['<svg xmlns="http://www.w3.org/2000/svg" width="1280" height="530" viewBox="0 0 1280 530">',
             '<rect width="1280" height="530" fill="white"/>',
             '<g fill="none" stroke="#444444" stroke-width="2">']
    for path in ["M270 90 H360", "M270 215 H315 V130 H360", "M270 340 H360",
                 "M590 130 H640 V215 H690", "M590 340 H640 V215 H690",
                 "M920 215 H960 V90 H1000", "M920 215 H1000", "M805 390 V250"]:
        parts.append(f'<path d="{path}"/>')
    parts.append('</g>')
    for x, y, title in nodes:
        parts.append(f'<rect x="{x}" y="{y}" width="230" height="70" rx="5" fill="#fafafa" stroke="#444444" stroke-width="2"/>')
        parts.append(f'<text x="{x+115}" y="{y+41}" text-anchor="middle" font-family="Arial" font-size="17" fill="#222222">{escape(title)}</text>')
    for x,y,label in [(280,77,"specs"),(288,327,"consulta"),(598,118,"REST"),(932,192,"dados"),(815,365,"evidências")]:
        parts.append(f'<text x="{x}" y="{y}" font-family="Arial" font-size="12" fill="#555555">{label}</text>')
    parts.append('</svg>')
    svg=''.join(parts)
    with pymupdf.open(stream=svg.encode(), filetype="svg") as document:
        document[0].get_pixmap(matrix=pymupdf.Matrix(1.5,1.5), alpha=False).save(TARGET)
    print(TARGET)


if __name__ == "__main__":
    build()
