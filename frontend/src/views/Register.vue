<template class="page-full mouse-bg">
  <div class="page-full mouse-bg">
    <div class="form-container">
      <div class="welcome-msg">
        <h1>{{ welcomeTitle }}</h1>
        <div class="welcome-rotating">
          <p>{{ currentSubtitle }}</p>
        </div>
      </div>

      <div class="register-container">
        <h1>Cadastro</h1>
        <form @submit.prevent="register">
          <div>
            <input v-model="name" type="text" placeholder="Nome" required />
          </div>

          <div>
            <input v-model="email" type="email" placeholder="Email" required />
          </div>

          <div>
            <input
              v-model="password"
              type="password"
              placeholder="Senha"
              required
            />
          </div>

          <button type="submit">Cadastrar</button>

          <div class="login-redirect">
            <a @click="goToLogin">Já tem uma conta?</a>
          </div>
        </form>

        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
        <p v-if="successMessage" class="success">{{ successMessage }}</p>
      </div>
    </div>

    <canvas id="trail"></canvas>
  </div>
</template>

<script>
import api from "../services/api.js";
import "../styles/LoginRegister/LoginRegister.css";
import { welcomeData } from "../data/welcome.js";

export default {
  name: "Register",

  data() {
    return {
      name: "",
      email: "",
      password: "",
      errorMessage: "",
      successMessage: "",
      welcomeTitle: welcomeData.title,
      subtitles: welcomeData.subtitle,
      currentSubtitle: welcomeData.subtitle[0],
      subtitleIndex: 0,
      typingSpeed: 70,
      deletingSpeed: 40,
      pauseTime: 1500,
    };
  },

  mounted() {
    const canvas = document.getElementById("trail");
    const ctx = canvas.getContext("2d");

    const resize = () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    };

    resize();
    window.addEventListener("resize", resize);

    let lastX = null;
    let lastY = null;

    const mouse = { x: 0, y: 0 };

    document.addEventListener("mousemove", (e) => {
      mouse.x = e.clientX;
      mouse.y = e.clientY;

      if (lastX === null) {
        lastX = e.clientX;
        lastY = e.clientY;
      }

      ctx.strokeStyle = "rgba(0,0,0,1)";
      ctx.lineWidth = 3;
      ctx.lineCap = "round";

      ctx.beginPath();
      ctx.moveTo(lastX, lastY);
      ctx.lineTo(e.clientX, e.clientY);
      ctx.stroke();

      lastX = e.clientX;
      lastY = e.clientY;
    });

    const fade = () => {
      ctx.fillStyle = "rgba(255,255,255,0.25)";
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      requestAnimationFrame(fade);
    };

    // ——————————————————————————
    // SNAKES + EXPLOSIONS (MESMO DO LOGIN)
    // ——————————————————————————

    const snakes = [];

    function createSnake() {
      const startX = Math.random() * canvas.width;
      const startY = Math.random() * canvas.height;

      return {
        points: Array.from({ length: 50 }, () => ({
          x: startX,
          y: startY,
        })),
        speed: 1 + Math.random() * 2,
        angle: Math.random() * Math.PI * 2,
        turnChance: 0.04 + Math.random() * 0.03,
        color: `rgba(${Math.floor(Math.random() * 255)},
                     ${Math.floor(Math.random() * 255)},
                     ${Math.floor(Math.random() * 255)},
                     ${0.3 + Math.random() * 0.4})`,
      };
    }

    for (let i = 0; i < 8; i++) snakes.push(createSnake());

    const particles = [];

    function explodeSnake(snake) {
      snake.points.forEach((p) => {
        for (let i = 0; i < 3; i++) {
          particles.push({
            x: p.x,
            y: p.y,
            vx: (Math.random() - 0.5) * 5,
            vy: (Math.random() - 0.5) * 5,
            alpha: 1,
            color: snake.color,
          });
        }
      });

      Object.assign(snake, createSnake());
    }

    function animateParticles() {
      particles.forEach((pt, i) => {
        pt.x += pt.vx;
        pt.y += pt.vy;
        pt.alpha -= 0.02;

        if (pt.alpha <= 0) {
          particles.splice(i, 1);
          return;
        }

        const rgb = pt.color.replace(/rgba?\(([^)]+)\)/, "$1").split(",");
        const r = rgb[0];
        const g = rgb[1];
        const b = rgb[2];

        ctx.fillStyle = `rgba(${r}, ${g}, ${b}, ${pt.alpha})`;
        ctx.beginPath();
        ctx.arc(pt.x, pt.y, 2, 0, Math.PI * 2);
        ctx.fill();
      });
    }

    function animateSnakes() {
      ctx.fillStyle = "rgba(255,255,255,0.06)";
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      animateParticles();

      snakes.forEach((snake) => {
        if (Math.random() < snake.turnChance) {
          snake.angle += (Math.random() - 0.5) * 0.6;
        }

        const head = snake.points[0];

        const dx = head.x - mouse.x;
        const dy = head.y - mouse.y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 200) {
          explodeSnake(snake);
          return;
        }

        const newHead = {
          x: head.x + Math.cos(snake.angle) * snake.speed,
          y: head.y + Math.sin(snake.angle) * snake.speed,
        };

        snake.points.unshift(newHead);
        snake.points.pop();

        if (
          newHead.x < -50 ||
          newHead.x > canvas.width + 50 ||
          newHead.y < -50 ||
          newHead.y > canvas.height + 50
        ) {
          Object.assign(snake, createSnake());
        }

        ctx.beginPath();
        ctx.strokeStyle = snake.color;
        ctx.lineWidth = 3;

        for (let i = 0; i < snake.points.length - 1; i++) {
          const p1 = snake.points[i];
          const p2 = snake.points[i + 1];
          ctx.moveTo(p1.x, p1.y);
          ctx.lineTo(p2.x, p2.y);
        }

        ctx.stroke();
      });

      requestAnimationFrame(animateSnakes);
    }

    animateSnakes();
    fade();
    this.typeWriter();
  },

  methods: {
    async register() {
      try {
        const response = await api.post("/users", {
          name: this.name,
          email: this.email,
          password: this.password,
        });

        if (response.status === 201 || response.status === 200) {
          const { user, token } = response.data;

          api.defaults.headers.Authorization = `Bearer ${token}`;
          localStorage.setItem("user", JSON.stringify(user));
          localStorage.setItem("token", token);

          this.successMessage =
            "Cadastro realizado! Redirecionando para o Dashboard...";
          this.errorMessage = "";

          setTimeout(() => {
            this.$router.push("/dashboard");
          }, 1000);
        }
      } catch (err) {
        if (err.response?.status === 409) {
          this.errorMessage = "Email já cadastrado. Tente outro.";
        } else {
          this.errorMessage = "Erro inesperado. Tente novamente.";
        }
        this.successMessage = "";
      }
    },

    goToLogin() {
      this.$router.push("/login");
    },

    typeWriter() {
      const current = this.subtitles[this.subtitleIndex];
      let i = 0;

      const type = () => {
        if (i <= current.length) {
          this.currentSubtitle = current.slice(0, i);
          i++;
          setTimeout(type, this.typingSpeed);
        } else {
          setTimeout(() => this.deleteWriter(), this.pauseTime);
        }
      };

      type();
    },

    deleteWriter() {
      const current = this.subtitles[this.subtitleIndex];
      let i = current.length;

      const del = () => {
        if (i >= 0) {
          this.currentSubtitle = current.slice(0, i);
          i--;
          setTimeout(del, this.deletingSpeed);
        } else {
          this.subtitleIndex = (this.subtitleIndex + 1) % this.subtitles.length;
          this.typeWriter();
        }
      };

      del();
    },
  },
};
</script>